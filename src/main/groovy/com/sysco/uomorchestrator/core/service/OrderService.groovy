package com.sysco.uomorchestrator.core.service

import com.google.inject.Inject
import com.sysco.uomdesktop.api.customer.AccountRepresentation
import com.sysco.uomdesktop.api.order.LineItemRepresentation
import com.sysco.uomdesktop.api.order.OrderRepresentation
import com.sysco.uomdesktop.client.IIBClient
import com.sysco.uomdesktop.client.iiborder.response.IIBOrderSubmitResponse
import com.sysco.uomdesktop.core.model.LineItem
import com.sysco.uomdesktop.core.model.Order
import com.sysco.uomdesktop.core.model.OrderStatus
import com.sysco.uomdesktop.exceptionmapper.IIBRequestFailedException
import com.sysco.uomdesktop.exceptionmapper.InvalidOrderException
import com.sysco.uomdesktop.repository.dao.OrderDao
import org.joda.time.DateTime
import org.joda.time.LocalDate

import javax.validation.ConstraintViolation
import javax.validation.Validation
import javax.validation.ValidatorFactory

import static com.sysco.uomdesktop.core.model.OrderStatus.OPEN

class OrderService {
    public static final String ORDER_CANNOT_BE_UDPATED = "ORDER_CANNOT_BE_UPDATED"

    private final OrderDao orderDao
    private final IIBClient iibClient
    private final GeneratesOrderReferenceNumbers generatesOrderReferenceNumbers
    private final ClockService clockService
    private final AccountService accountService
    private final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()

    @Inject
    OrderService(OrderDao orderDao, IIBClient iibClient, ClockService clockService,
                 GeneratesOrderReferenceNumbers generatesOrderReferenceNumbers, AccountService accountService) {
        this.generatesOrderReferenceNumbers = generatesOrderReferenceNumbers
        this.orderDao = orderDao
        this.iibClient = iibClient
        this.clockService = clockService
        this.accountService = accountService
    }

    void submit(UUID id) {
        Order order = orderDao.findOne(id)

        LocalDate deliveryDate = accountService.getNextAvailableDeliveryDate(order.accountId)
        AccountRepresentation account = accountService.getById(order.accountId)

        Set<ConstraintViolation<Order>> violations = validatorFactory.getValidator().validate(order);
        if (violations.size() > 0) {
            throw new InvalidOrderException(violations*.message);
        }

        DateTime submitDate = clockService.currentDateTime()

        IIBOrderSubmitResponse response = iibClient.submitOrder(order, submitDate, deliveryDate, account)
        if (response.isFailure()) {
            throw new IIBRequestFailedException(response.validationResult())
        }

        orderDao.submit(id, OrderStatus.SUBMITTED, submitDate, deliveryDate)
    }

    OrderRepresentation open(UUID accountId) {
        Order order = new Order(
            status: OPEN,
            referenceNumber: generatesOrderReferenceNumbers.next(),
            accountId: accountId
        )
        UUID id = orderDao.insert(order)

        new OrderRepresentation(id: id, accountId: accountId, referenceNumber: order.referenceNumber, status: order.status, lineItems: [])
    }

    List<OrderRepresentation> findAllByAccount(UUID accountId) {
        orderDao
            .findAllByAccountId(accountId)
            .collect { order -> new OrderRepresentation(order) }
    }

    List<OrderRepresentation> getOrdersByStatus(List<OrderStatus> statuses) {
        List<Order> orders = orderDao.getOrdersByStatus(statuses)
        orders.collect { order -> new OrderRepresentation(order) }
    }

    List<LineItemRepresentation> updateLineItems(UUID orderId, List<LineItem> lineItems) {
        orderDao.upsertLineItem(lineItems, orderId, lineItems*.product.id)
        lineItems.collect { lineItem -> new LineItemRepresentation(lineItem) }
    }

    void confirm(String referenceNumber, String confirmationNumber, LocalDate deliveryDate) {
        orderDao.confirmByReferenceNumber(referenceNumber, OrderStatus.CONFIRMED, confirmationNumber, deliveryDate)
    }

    void deleteLineItem(UUID id, UUID lineItemId) {
        Order order = orderDao.findById(id)
        if (OPEN.equals(order.status)) {
            orderDao.deleteLineItem(id, lineItemId)
        } else {
            throw new InvalidOrderException([ORDER_CANNOT_BE_UDPATED])
        }
    }
}
