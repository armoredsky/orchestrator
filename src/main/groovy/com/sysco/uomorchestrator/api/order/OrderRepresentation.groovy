package com.sysco.uomorchestrator.api.order

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
    String accountId

    public OrderRepresentation() {}

    boolean orderIsOpen() {
        status == OrderStatus.OPEN.name()
    }
}
