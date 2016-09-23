package com.sysco.uomorchestrator.exceptionmapper

import com.sysco.uomorchestrator.api.ErrorRepresentation
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.ws.rs.core.Response
import javax.ws.rs.ext.ExceptionMapper

class IIBRequestFailedExceptionMapper implements ExceptionMapper<IIBRequestFailedException> {
    private static final Logger LOG = LoggerFactory.getLogger(IIBRequestFailedExceptionMapper)

    @Override
    Response toResponse(IIBRequestFailedException exception) {
        LOG.error(exception.getMessage(), exception)

        Response
            .status(Response.Status.BAD_REQUEST)
            .entity(new ErrorRepresentation('IIB_REQUEST_FAILED'))
            .build()
    }
}
