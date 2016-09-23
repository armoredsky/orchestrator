package com.sysco.uomdesktop.exceptionmapper

import com.sysco.uomorchestrator.exceptionmapper.InvalidOrderException
import com.sysco.uomorchestrator.exceptionmapper.InvalidOrderExceptionMapper
import spock.lang.Specification

import javax.ws.rs.core.Response

public class InvalidOrderExceptionMapperTest extends Specification {
    InvalidOrderExceptionMapper invalidOrderExceptionMapper

    void setup() {
        invalidOrderExceptionMapper = new InvalidOrderExceptionMapper()
    }

    void 'should map to a 400 status code and send the validationResult as the entity'() {
        given:
        def errorMessages = ["TOTAL_QUANTITY_EXCEEDED_MAX"]

        when:
        Response response = invalidOrderExceptionMapper.toResponse(new InvalidOrderException(errorMessages))

        then:
        response.status == 400
        response.entity.errors == ['TOTAL_QUANTITY_EXCEEDED_MAX']
    }
}
