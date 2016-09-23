package com.sysco.uomorchestrator.client.iiborder.response

import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode
class IIBOrderSubmitAcknowledgement {
    IIBOrderSubmitValidationResult ValidationResult
    Map FailedOrderDetails
}
