package com.sysco.uomorchestrator.client.iiborder.response

import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode
class IIBOrderSubmitValidationResult {
    String RequestStatus
    Integer TotalNoOrders
    Integer SuccessfulOrders
    String RequestTrackerID
}
