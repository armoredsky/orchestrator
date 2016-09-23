package com.sysco.uomdesktop.exceptionmapper

import com.sysco.uomorchestrator.client.iiborder.response.IIBOrderSubmitValidationResult
import com.sysco.uomorchestrator.exceptionmapper.IIBRequestFailedException
import com.sysco.uomorchestrator.exceptionmapper.IIBRequestFailedExceptionMapper
import spock.lang.Specification

import javax.ws.rs.core.Response

class IIBRequestFailedExceptionMapperTest extends Specification {
    IIBRequestFailedExceptionMapper iibRequestFailedExceptionMapper

    void setup() {
        iibRequestFailedExceptionMapper = new IIBRequestFailedExceptionMapper()
    }

    void 'should map to a 400 status code and send the validationResult as the entity'() {
        given:
        IIBOrderSubmitValidationResult validationResult = Stub(IIBOrderSubmitValidationResult)

        when:
        Response response = iibRequestFailedExceptionMapper.toResponse(new IIBRequestFailedException(validationResult))

        then:
        response.status == 400
        response.entity.errors == ['IIB_REQUEST_FAILED']
    }
}
