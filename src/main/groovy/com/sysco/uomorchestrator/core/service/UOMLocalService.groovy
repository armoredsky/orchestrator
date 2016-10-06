package com.sysco.uomorchestrator.core.service

import com.google.inject.Inject
import com.sysco.uomorchestrator.api.uomlocal.UOMLocalAccountRepresentation
import com.sysco.uomorchestrator.client.OrderServiceClient

class UOMLocalService {

    @Inject
    OrderServiceClient client

    UOMLocalAccountRepresentation loadAccount(String customerNumber) {
       return client.getAccount(customerNumber)
    }
}
