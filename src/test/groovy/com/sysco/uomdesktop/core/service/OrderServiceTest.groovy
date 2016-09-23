package com.sysco.uomdesktop.core.service

import com.sysco.uomorchestrator.api.common.ProductRepresentation
import com.sysco.uomorchestrator.api.customer.AccountRepresentation
import com.sysco.uomorchestrator.api.order.LineItemRepresentation
import com.sysco.uomorchestrator.api.order.OrderRepresentation
import com.sysco.uomorchestrator.client.IIBClient
import com.sysco.uomorchestrator.client.iiborder.response.IIBOrderSubmitResponse
import com.sysco.uomorchestrator.core.model.LineItem
import com.sysco.uomorchestrator.core.model.Order
import com.sysco.uomorchestrator.core.model.OrderStatus
import com.sysco.uomorchestrator.core.model.Product
import com.sysco.uomorchestrator.core.service.AccountService
import com.sysco.uomorchestrator.core.service.ClockService
import com.sysco.uomorchestrator.core.service.GeneratesOrderReferenceNumbers
import com.sysco.uomorchestrator.core.service.OrderService
import com.sysco.uomorchestrator.exceptionmapper.IIBRequestFailedException
import com.sysco.uomorchestrator.exceptionmapper.InvalidOrderException
import com.sysco.uomorchestrator.repository.dao.OrderDao
import org.joda.time.DateTime
import org.joda.time.LocalDate
import spock.lang.Specification

import static OrderStatus.OPEN
import static OrderStatus.SUBMITTED
import static com.sysco.uomdesktop.support.mothers.AccountMother.anAccountRepresentation
import static com.sysco.uomdesktop.support.mothers.LineItemMother.aLineItem
import static com.sysco.uomdesktop.support.mothers.OrderMother.anOrder
import static java.util.UUID.randomUUID
import static org.joda.time.DateTimeZone.UTC
import static org.joda.time.LocalDate.now

class OrderServiceTest extends Specification {

    private IIBClient mockIibClient
    private OrderDao mockOrderDao
    private GeneratesOrderReferenceNumbers generatesOrderReferenceNumbers
    private ClockService mockClockService
    private AccountService mockAccountService
    private OrderService orderService

    void setup() {
        mockIibClient = Mock(IIBClient)
        mockOrderDao = Mock(OrderDao)
        mockAccountService = Mock(AccountService)
        generatesOrderReferenceNumbers = Mock(GeneratesOrderReferenceNumbers)
        mockClockService = Mock(ClockService)

        orderService = new OrderService(mockOrderDao, mockIibClient, mockClockService, generatesOrderReferenceNumbers, mockAccountService)
    }

    void 'retrieves all orders by status'() {
        given:
        UUID orderOneId = randomUUID()
        UUID orderTwoId = randomUUID()
        mockOrderDao.getOrdersByStatus([OPEN, SUBMITTED]) >> [anOrder(id: orderOneId), anOrder(id: orderTwoId)]

        when:
        List<OrderRepresentation> orderRepresentations = orderService.getOrdersByStatus([OPEN, SUBMITTED])

        then:
        orderRepresentations.size() == 2
        orderRepresentations*.id.contains(orderOneId.toString())
        orderRepresentations*.id.contains(orderTwoId.toString())
    }

    void 'opens an order'() {
        given:
        UUID orderId = randomUUID()
        UUID accountId = randomUUID()
        generatesOrderReferenceNumbers.next() >> '1234567'

        when:
        OrderRepresentation orderRepresentation = orderService.open(accountId)

        then:
        1 * mockOrderDao.insert({ orderToInsert ->
            orderToInsert.status == OrderStatus.OPEN
            orderToInsert.referenceNumber == '1234567'
        }) >> orderId

        orderRepresentation.id == orderId.toString()
        orderRepresentation.accountId == accountId
        orderRepresentation.status == OrderStatus.OPEN.name()
        orderRepresentation.referenceNumber == '1234567'
        orderRepresentation.lineItems == []
    }

    void '#submit: submits the order with IIB'() {
        given:
        DateTime submitDate = new DateTime()
        mockClockService.currentDateTime() >> submitDate
        UUID id = randomUUID()
        LocalDate deliveryDate = now(UTC)

        and:
        Order order = anOrder()
        mockOrderDao.findOne(id) >> order
        mockAccountService.getNextAvailableDeliveryDate(order.accountId) >> deliveryDate
        AccountRepresentation account = anAccountRepresentation()
        mockAccountService.getById(order.accountId) >> account

        when:
        orderService.submit(id)

        then:
        IIBOrderSubmitResponse iibResponse = Stub(IIBOrderSubmitResponse) { isFailure() >> false }
        1 * mockIibClient.submitOrder(order, submitDate, deliveryDate, account) >> iibResponse
    }

    void '#submit: updates the order status to submitted, sets a submit date and delivery date'() {
        given:
        DateTime submitDate = new DateTime()
        mockClockService.currentDateTime() >> submitDate
        UUID id = randomUUID()
        LocalDate deliveryDate = now(UTC)

        and:
        Order order = anOrder()
        mockOrderDao.findOne(id) >> order
        mockAccountService.getNextAvailableDeliveryDate(order.accountId) >> deliveryDate
        AccountRepresentation account = anAccountRepresentation()
        mockAccountService.getById(order.accountId) >> account

        and:
        IIBOrderSubmitResponse iibResponse = Stub(IIBOrderSubmitResponse) { isFailure() >> false }
        mockIibClient.submitOrder(_ as Order, _ as DateTime, _ as LocalDate, account) >> iibResponse

        when:
        orderService.submit(id)

        then:
        1 * mockOrderDao.submit(id, SUBMITTED, submitDate, deliveryDate)
    }

    void '#submit: throws an IIBRequestFailedException when order fails to submit to IIB'() {
        given:
        UUID id = randomUUID()
        DateTime submitDate = new DateTime()
        mockClockService.currentDateTime() >> submitDate
        LocalDate deliveryDate = now(UTC)

        and:
        Order order = anOrder()
        mockOrderDao.findOne(id) >> order
        mockAccountService.getNextAvailableDeliveryDate(order.accountId) >> deliveryDate
        mockAccountService.getById(order.accountId) >> anAccountRepresentation()

        and:
        IIBOrderSubmitResponse iibResponse = Stub(IIBOrderSubmitResponse) { isFailure() >> true }
        mockIibClient.submitOrder(_ as Order, _ as DateTime, _ as LocalDate, _ as AccountRepresentation) >> iibResponse

        when:
        orderService.submit(id)

        then:
        0 * mockOrderDao.submit(order.id, SUBMITTED, submitDate, deliveryDate)
        thrown(IIBRequestFailedException)
    }

    void '#submit: throws an InvalidOrderException when the total number of items is over 10000'() {
        given:
        UUID id = randomUUID()
        Order order = anOrder(lineItems: [aLineItem(quantity: 10000)])
        mockOrderDao.findOne(id) >> order

        when:
        orderService.submit(id)

        then:
        0 * mockOrderDao.submit(_ as UUID, SUBMITTED, _ as DateTime, _ as LocalDate)
        thrown(InvalidOrderException)
    }

    void '#submit: throws an InvalidOrderException when the total price is over 10000000'() {
        given:
        UUID id = randomUUID()
        Order order = anOrder(lineItems: [aLineItem(price: 10000000)])
        mockOrderDao.findOne(id) >> order

        when:
        orderService.submit(id)

        then:
        0 * mockOrderDao.submit(_ as UUID, SUBMITTED, _ as DateTime, _ as LocalDate)
        thrown(InvalidOrderException)
    }

    void '#update successfully upserts the list of line items'() {
        given:
        UUID orderId = randomUUID()

        ProductRepresentation productOne = new ProductRepresentation(id: randomUUID(), supc: "12345")
        LineItem lineItem = new LineItem([id: randomUUID(), product: new Product(productOne), quantity: 3, price: 9.99])
        LineItemRepresentation expectedLineItemRepresentation = new LineItemRepresentation(lineItem)

        ProductRepresentation productTwo = new ProductRepresentation(id: randomUUID(), supc: "87654")
        LineItem lineItemTwo = new LineItem([id: randomUUID(), product: new Product(productTwo), quantity: 3, price: 9.99])
        LineItemRepresentation expectedLineItemRepresentationTwo = new LineItemRepresentation(lineItemTwo)

        when:
        List<LineItemRepresentation> lineItems = orderService.updateLineItems(orderId, [lineItem, lineItemTwo])

        then:
        1 * mockOrderDao.upsertLineItem([lineItem, lineItemTwo], orderId, [lineItem.product.id, lineItemTwo.product.id])
        lineItems.size() == 2
        lineItems.containsAll(expectedLineItemRepresentation, expectedLineItemRepresentationTwo)
    }

    void '#confirm updates the status to confirmed and sets the confirmation number'() {
        given:
        LocalDate deliveryDate = now()

        when:
        orderService.confirm('1234567', '00001234', deliveryDate)

        then:
        1 * mockOrderDao.confirmByReferenceNumber('1234567', OrderStatus.CONFIRMED, '00001234', deliveryDate)
    }

    void '#delete calls dao to delete line item from order'() {
        given:
        UUID orderId = randomUUID()
        UUID lineItemId = randomUUID()

        and:
        Order order = anOrder(id: orderId, status: OPEN)
        mockOrderDao.findById(orderId) >> order

        when:
        orderService.deleteLineItem(orderId, lineItemId)

        then:
        1 * mockOrderDao.deleteLineItem(orderId, lineItemId)
    }

    void '#delete throws an InvalidOrderException when trying to delete a line item from an order that is not open'() {
        given:
        UUID orderId = randomUUID()
        UUID lineItemId = randomUUID()

        and:
        Order order = anOrder(id: orderId, status: SUBMITTED)
        mockOrderDao.findById(orderId) >> order

        when:
        orderService.deleteLineItem(orderId, lineItemId)

        then:
        InvalidOrderException exception = thrown()
        exception.errors.first() == "ORDER_CANNOT_BE_UPDATED"
    }
}
