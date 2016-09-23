package com.sysco.uomdesktop.client.iiborder.response

import com.sysco.uomorchestrator.client.iiborder.response.IIBOrderSubmitAcknowledgement
import com.sysco.uomorchestrator.client.iiborder.response.IIBOrderSubmitResponse
import com.sysco.uomorchestrator.client.iiborder.response.IIBOrderSubmitValidationResult
import spock.lang.Specification

class IIBOrderSubmitResponseTest extends Specification {
    void 'is it a failure when the response indicates a validation failure'() {
        when:
        IIBOrderSubmitResponse iibResponse = new IIBOrderSubmitResponse([
            Acknowledgement: new IIBOrderSubmitAcknowledgement([
                ValidationResult: new IIBOrderSubmitValidationResult([
                    RequestStatus: 'Fail',
                    SuccessfulOrders: 0
                ])
            ])
        ])

        then:
        iibResponse.isFailure() == true
    }

    void 'is not a failure when the response does not indicate a validation failure'() {
        when:
        IIBOrderSubmitResponse iibResponse = new IIBOrderSubmitResponse([
            Acknowledgement: new IIBOrderSubmitAcknowledgement([
                ValidationResult: new IIBOrderSubmitValidationResult([
                    RequestStatus: 'Success',
                ])
            ])
        ])

        then:
        iibResponse.isFailure() == false
    }
}
