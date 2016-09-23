package com.sysco.uomorchestrator.client

import com.google.inject.Inject
import com.sysco.uomorchestrator.api.customer.AccountRepresentation
import com.sysco.uomorchestrator.client.iiborder.response.IIBOrderSubmitResponse
import com.sysco.uomorchestrator.core.model.Order
import com.sysco.uomorchestrator.exceptionmapper.GatewayTimeoutException
import com.sysco.uomorchestrator.api.customer.AccountRepresentation
import com.sysco.uomorchestrator.client.iiborder.response.IIBOrderSubmitResponse
import com.sysco.uomorchestrator.core.model.Order
import com.sysco.uomorchestrator.exceptionmapper.GatewayTimeoutException
import groovyx.net.http.RESTClient
import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class IIBClient {
    private static final Logger LOG = LoggerFactory.getLogger(IIBClient)

    private final RESTClient client
    private final IIBClientRequests iibClientRequests

    @Inject
    IIBClient(RESTClient restClient, IIBClientRequests iibClientRequests) {
        this.iibClientRequests = iibClientRequests
        this.client = restClient
    }

    IIBOrderSubmitResponse submitOrder(Order order, DateTime submitDate, LocalDate deliveryDate, AccountRepresentation account) {
        def response = submitOrderToIIB(iibClientRequests.submitOrder(order, submitDate, deliveryDate, account))

        new IIBOrderSubmitResponse((Map) response.getData())
    }

    private Object submitOrderToIIB(Map requestBody) {
        def response;

        LOG.info("SubmitOrder REQUEST: \n ${requestBody.toString()}")
        try {
            response = client.post(
                path: '/services/ordersubmit',
                body: requestBody,
                requestContentType: 'application/json'
            )
            LOG.info("SubmitOrder RESPONSE: \n ${response.getData().toString()}")

            return response
        } catch (Exception e) {
            throw new GatewayTimeoutException('Unable to submit order to IIB', e)
        }
    }
}
