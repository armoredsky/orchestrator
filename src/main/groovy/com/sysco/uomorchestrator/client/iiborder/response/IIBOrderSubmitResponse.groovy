package com.sysco.uomorchestrator.client.iiborder.response

import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode
class IIBOrderSubmitResponse {
    IIBOrderSubmitAcknowledgement Acknowledgement

    boolean isFailure() {
        validationResult().RequestStatus == 'Fail' && validationResult().SuccessfulOrders == 0
    }

    IIBOrderSubmitValidationResult validationResult() {
        Acknowledgement.ValidationResult
    }
}
