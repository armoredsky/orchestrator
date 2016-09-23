package com.sysco.uomorchestrator.exceptionmapper

class GatewayTimeoutException extends RuntimeException {
    GatewayTimeoutException(String message) {
        super(message)
    }

    GatewayTimeoutException(String message, Throwable cause) {
        super(message, cause)
    }
}
