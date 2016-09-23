package com.sysco.uomdesktop.dao

import com.sysco.uomdesktop.builder.LineItemBuilder
import com.sysco.uomorchestrator.core.model.LineItem
import com.sysco.uomorchestrator.core.model.Order
import com.sysco.uomorchestrator.core.model.Product
import com.sysco.uomdesktop.dao.support.TestOnlyAccountDao
import com.sysco.uomdesktop.dao.support.TestOnlyOrderDao
import com.sysco.uomorchestrator.repository.dao.OrderDao
import com.sysco.uomorchestrator.repository.dao.ProductDao
import com.sysco.uomorchestrator.repository.jdbi.JodaDateTimeArgumentFactory
import com.sysco.uomorchestrator.repository.jdbi.JodaLocalDateArgumentFactory
import com.sysco.uomdesktop.support.IntegrationTestBase
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.LocalDate

import java.sql.Timestamp

import static com.sysco.uomorchestrator.core.model.OrderStatus.*
import static com.sysco.uomdesktop.support.mothers.LineItemMother.aLineItem
import static com.sysco.uomdesktop.support.mothers.OrderMother.aSubmittedOrder
import static com.sysco.uomdesktop.support.mothers.OrderMother.anOrder
import static com.sysco.uomdesktop.support.mothers.ProductMother.aProduct

class OrderDaoTest extends IntegrationTestBase {
    ProductDao productDao
    OrderDao orderDao
    TestOnlyOrderDao testOrderDao
    TestOnlyAccountDao testOnlyAccountDao

    UUID accountId

    void setup() {
        productDao = dbi.onDemand(ProductDao)
        orderDao = dbi.onDemand(OrderDao)
        dbi.registerArgumentFactory(new JodaDateTimeArgumentFactory())
        dbi.registerArgumentFactory(new JodaLocalDateArgumentFactory())
        testOrderDao = dbi.onDemand(TestOnlyOrderDao)
        testOnlyAccountDao = dbi.onDemand(TestOnlyAccountDao)

        accountId = anAccountExists()
    }

    void 'finds a single order by id'() {
        given:
        UUID orderId = orderDao.insert(anOrder(accountId: accountId))

        when:
        Order order = orderDao.findById(orderId)

        then:
        order != null
        order.id == orderId
    }

    void 'finds a single order by id, including its line items'() {
        given:
        Product product = aProduct()
        UUID productId = productDao.insert(product)

        UUID orderId = orderDao.insert(anOrder(accountId: accountId))

        LineItem lineItem = aLineItem(product: aProduct(id: productId))
        testOrderDao.insertLineItem(lineItem, orderId, productId)

        when:
        Order order = orderDao.findOne(orderId)

        then:
        order.id == orderId
        order.lineItems[0].id == lineItem.id
        order.lineItems[0].product.id == productId
        order.lineItems[0].product.supc == product.supc
    }

    void '#findAllByAcocuntId: finds all orders by an account id, including line items'() {
        given:
        UUID accountId = anAccountExists()
        UUID otherAccountId = anAccountExists()

        and:
        UUID orderId1 = orderDao.insert(anOrder(accountId: accountId))
        UUID orderId2 = orderDao.insert(anOrder(accountId: accountId))
        orderDao.insert(anOrder(accountId: otherAccountId))

        and:
        UUID productId = productDao.insert(aProduct())
        testOrderDao.insertLineItem(aLineItem(), orderId1, productId)
        testOrderDao.insertLineItem(aLineItem(), orderId2, productId)

        when:
        List<Order> orders = orderDao.findAllByAccountId(accountId)

        then:
        orders.size() == 2
        orders*.id.containsAll(orderId1, orderId2)
        !orders[0].lineItems.isEmpty()
        orders[0].lineItems[0].product instanceof Product
        !orders[1].lineItems.isEmpty()
        orders[1].lineItems[0].product instanceof Product
    }

    void 'inserts an order'() {
        when:
        Order orderToInsert = anOrder(id: null, status: OPEN, referenceNumber: '1234567', accountId: accountId)
        UUID createdOrderId = orderDao.insert(orderToInsert)

        then:
        Order foundOrder = orderDao.findById(createdOrderId)
        foundOrder.id == createdOrderId
        foundOrder.status == OPEN
        foundOrder.referenceNumber == '1234567'

    }

    void 'fetches orders by status, sorted by delivery date'() {
        given:
        LocalDate olderDeliveryDate = new LocalDate().minusDays(2)
        Order olderOrder = aSubmittedOrder(deliveryDate: olderDeliveryDate, accountId: accountId)
        UUID olderOrderId = orderDao.insert(olderOrder)

        Order newerOrder = anOrder(status: CONFIRMED, deliveryDate: olderDeliveryDate.plusDays(2), accountId: accountId)
        UUID newerOrderId = orderDao.insert(newerOrder)

        when:
        List<Order> orders = orderDao.getOrdersByStatus([SUBMITTED, CONFIRMED])

        then:
        orders.size() == 2
        orders[0].id == newerOrderId
        orders[0].status == CONFIRMED
        orders[1].id == olderOrderId
        orders[1].status == SUBMITTED
    }

    void 'fetches orders with line items'() {
        given:
        UUID orderId = orderDao.insert(aSubmittedOrder(accountId: accountId))

        and:
        Product product = aProduct()
        UUID productId = productDao.insert(product)
        product.id = productId

        and:
        LineItem lineItem = aLineItem(product: product)
        testOrderDao.insertLineItem(lineItem, orderId, productId)

        when:
        List<Order> orders = orderDao.getOrdersByStatus([SUBMITTED])

        then:
        orders.size() == 1
        orders[0].id == orderId
        orders[0].lineItems.size() == 1
        orders[0].lineItems[0] == lineItem
        orders[0].lineItems[0].product == product
    }

    void 'updates an existing order'() {
        given:
        DateTime submitDate = new DateTime().withZone(DateTimeZone.UTC)
        Timestamp expectedTimestamp = new Timestamp(submitDate.millis)

        UUID orderId = orderDao.insert(anOrder(accountId: accountId))
        Order order = new Order(id: orderId, status: SUBMITTED, submitDate: submitDate)

        when:
        orderDao.update(order)

        and:
        String getOrderByIdQuery = """
            select * from uom.order where id='${orderId}'
        """
        List orders = executeSqlQuery(getOrderByIdQuery)

        then:
        orders.size() == 1
        orders[0].id == orderId
        orders[0].status == SUBMITTED.name()
        orders[0].submit_date == expectedTimestamp
    }

    void 'updates existing line item with new quantity'() {
        given:
        UUID orderId = orderDao.insert(anOrder(accountId: accountId))
        UUID lineItemId = UUID.randomUUID()

        Product product = aProduct([id: UUID.randomUUID()])
        product.id = productDao.insert(product)

        LineItem existingLineItem =  aLineItem([id: lineItemId, product: product, quantity: 4])
        LineItem updatedLineItem =  aLineItem([id: lineItemId, product: product, quantity: 5])
        testOrderDao.insertLineItem(existingLineItem, orderId, existingLineItem.product.id)

        when:
        orderDao.upsertLineItem([updatedLineItem], orderId, [existingLineItem.product.id])

        List<LineItem> actualLineItems = orderDao.getOrderLineItems(orderId)

        then:
        actualLineItems.size() == 1
        actualLineItems[0].quantity == 5
    }

    void 'inserts new line item when line item does not already exist'() {
        given:
        UUID newLineItemId = UUID.randomUUID()
        UUID orderId = orderDao.insert(anOrder(accountId: accountId))
        LineItem newLineItem = new LineItemBuilder().withQuantity(2).withId(newLineItemId).build()

        String insertNewLineItemProductQuery = """
            insert into uom.product values
                (\'${newLineItem.product.id}\', \'DONUT CAKE BLUEBERRY\', \'4361432\', \'SYS CLS\', \'6\', \'5 LB\')
            returning id
            """
        executeSqlQuery(insertNewLineItemProductQuery)

        when:
        orderDao.upsertLineItem([newLineItem], orderId, [newLineItem.product.id])

        List<LineItem> actualLineItems = orderDao.getOrderLineItems(orderId)

        then:
        actualLineItems.size() == 1
        actualLineItems[0].quantity == 2
    }

    void "submits an order by settings its submit date and status"() {
        given:
        UUID id = orderDao.insert(anOrder(accountId: accountId))
        DateTime submitDate = new DateTime().withZone(DateTimeZone.UTC)
        LocalDate deliveryDate = new LocalDate()

        when:
        orderDao.submit(id, SUBMITTED, submitDate, deliveryDate)

        then:
        Order order = orderDao.findById(id)
        order.status == SUBMITTED
        order.submitDate == submitDate
        order.deliveryDate == deliveryDate
    }

    void "#confirmByReferenceNumber: confirms an order by setting its status and its confirmation number"() {
        given:
        UUID id = orderDao.insert(anOrder(referenceNumber: '3333333', accountId: accountId))
        LocalDate deliveryDate = LocalDate.now()

        when:
        orderDao.confirmByReferenceNumber('3333333', CONFIRMED, '00001234', deliveryDate)

        then:
        Order order = orderDao.findById(id)
        order.status == CONFIRMED
        order.confirmationNumber == '00001234'
        order.deliveryDate == deliveryDate
    }

    void "#delete removes an order_line_item"() {
        given:
        Product product = aProduct()
        UUID productId = productDao.insert(product)
        UUID lineItemId = UUID.randomUUID()
        LineItem lineItem = aLineItem(id: lineItemId, product: product)
        UUID id = orderDao.insert(anOrder(accountId: accountId))
        testOrderDao.insertLineItem(lineItem, id, productId)

        when:
        orderDao.deleteLineItem(id, lineItemId)

        then:
        List<LineItem> lineItems = orderDao.getOrderLineItems(id)
        lineItems.size() == 0
        !lineItems.contains(lineItem)
    }
}
