package com.sysco.uomorchestrator.api.customer

import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode
class AccountRepresentation {
    UUID id
    String nextAvailableDeliveryDate
    String opCo
    String customerNumber
    String customerName
}
