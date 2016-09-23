package com.sysco.uomorchestrator.exceptionmapper

import com.sysco.uomorchestrator.api.ErrorRepresentation

import javax.ws.rs.core.Response
import javax.ws.rs.ext.ExceptionMapper

class InvalidOrderExceptionMapper implements ExceptionMapper<InvalidOrderException> {
    @Override
    Response toResponse(InvalidOrderException exception) {
        Response
            .status(Response.Status.BAD_REQUEST)
            .entity(new ErrorRepresentation(exception.errors))
            .build()
    }
}
