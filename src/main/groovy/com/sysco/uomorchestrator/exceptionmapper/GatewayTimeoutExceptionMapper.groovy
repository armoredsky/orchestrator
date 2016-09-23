package com.sysco.uomorchestrator.exceptionmapper

import com.sysco.uomorchestrator.api.ErrorRepresentation
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.ws.rs.core.Response
import javax.ws.rs.ext.ExceptionMapper

class GatewayTimeoutExceptionMapper implements ExceptionMapper<GatewayTimeoutException> {
    private static final Logger LOG = LoggerFactory.getLogger(GatewayTimeoutExceptionMapper)

    @Override
    Response toResponse(GatewayTimeoutException exception) {
        LOG.error(exception.getMessage(), exception)

        Response
            .status(Response.Status.GATEWAY_TIMEOUT)
            .entity(new ErrorRepresentation('GATEWAY_TIMEOUT'))
            .build()
    }
}
