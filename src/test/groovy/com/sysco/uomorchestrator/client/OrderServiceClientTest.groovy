package com.sysco.uomorchestrator.client

import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.RESTClient
import spock.lang.Specification

class OrderServiceClientTest extends Specification {

    RESTClient mockRestClient = Mock(RESTClient)
    OrderServiceClient orderClient = new OrderServiceClient(restClient: mockRestClient)

    void 'get request contains customerNumber query param'() {
        given:
        String customerNumber = "1234567"

        HttpResponseDecorator orderServiceResponse = Stub(HttpResponseDecorator) {
            getData() >> [:]
        }

        when:
        orderClient.getAccount(customerNumber)

        then:
        1 * mockRestClient.get({
            it.query == [customerNumber: customerNumber]
        }) >> orderServiceResponse
    }
}
