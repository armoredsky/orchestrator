package com.sysco.uomorchestrator.api.order

import com.sysco.uomorchestrator.core.model.Order
import com.sysco.uomorchestrator.core.model.OrderStatus
import com.sysco.uomorchestrator.core.model.Order
import com.sysco.uomorchestrator.core.model.OrderStatus
import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode
class OrderRepresentation {
    String id
    String referenceNumber
    String confirmationNumber
    String status
    String submitDate
    String deliveryDate
    List<LineItemRepresentation> lineItems
    UUID accountId

    public OrderRepresentation() {}

    public OrderRepresentation(Order order) {
        this.id = order.id
        this.referenceNumber = order.referenceNumber
        this.confirmationNumber = order.confirmationNumber
        this.status = order.status
        this.submitDate = order.submitDate
        this.deliveryDate = order.deliveryDate
        this.lineItems = order.lineItems.collect { lineItem -> new LineItemRepresentation(lineItem) }
        this.accountId = order.accountId
    }

    boolean orderIsOpen() {
        status == OrderStatus.OPEN.name()
    }
}
