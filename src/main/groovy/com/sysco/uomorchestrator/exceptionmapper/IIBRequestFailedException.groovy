package com.sysco.uomorchestrator.exceptionmapper

import com.sysco.uomorchestrator.client.iiborder.response.IIBOrderSubmitValidationResult

class IIBRequestFailedException extends RuntimeException {

    IIBOrderSubmitValidationResult validationResult

    IIBRequestFailedException(IIBOrderSubmitValidationResult validationResult) {
        this.validationResult = validationResult
    }
}
