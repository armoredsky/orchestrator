package com.sysco.uomdesktop.exceptionmapper

import com.sysco.uomorchestrator.exceptionmapper.GatewayTimeoutException
import com.sysco.uomorchestrator.exceptionmapper.GatewayTimeoutExceptionMapper
import spock.lang.Specification

import javax.ws.rs.core.Response

class GatewayTimeoutExceptionMapperTest extends Specification {
    GatewayTimeoutExceptionMapper gatewayTimeoutExceptionMapper

    void setup() {
        gatewayTimeoutExceptionMapper = new GatewayTimeoutExceptionMapper()
    }

    void 'should map to a 504 status code'() {
        when:
        Response response = gatewayTimeoutExceptionMapper.toResponse(new GatewayTimeoutException('the message'))

        then:
        response.status == 504
        response.entity.errors == ['GATEWAY_TIMEOUT']
    }
}
