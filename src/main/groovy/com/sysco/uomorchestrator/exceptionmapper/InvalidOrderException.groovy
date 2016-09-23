package com.sysco.uomorchestrator.exceptionmapper

class InvalidOrderException extends RuntimeException {

    List<String> errors

    InvalidOrderException(List<String> errors) {
        this.errors = errors
    }
}
