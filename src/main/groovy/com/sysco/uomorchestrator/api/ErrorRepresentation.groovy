package com.sysco.uomorchestrator.api

class ErrorRepresentation {
    List<String> errors;

    ErrorRepresentation(String... errors) {
        this.errors = errors;
    }

    ErrorRepresentation(List<String> errors) {
        this.errors = errors;
    }
}
