package com.sysco.uomorchestrator.client

import com.google.inject.Inject
import com.sysco.uomorchestrator.api.uomlocal.UOMLocalAccountRepresentation
import groovyx.net.http.RESTClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class OrderServiceClient {
    private static final Logger LOG = LoggerFactory.getLogger(OrderServiceClient)

    @Inject
    RESTClient restClient

    UOMLocalAccountRepresentation getAccount(customerNumber){
        def request = [
            path : '/api/uomlocal-gateway/accounts',
            query: [
                customerNumber: customerNumber
            ],
            requestContentType: 'application/json'
        ]

        LOG.info("REQUEST: \n\n ${request} \n\n")

        def response = restClient.get(request)

        LOG.info("RESPONSE: \n\n ${response.getData()} \n\n")

       return new UOMLocalAccountRepresentation((Map) response.getData())
    }
}
