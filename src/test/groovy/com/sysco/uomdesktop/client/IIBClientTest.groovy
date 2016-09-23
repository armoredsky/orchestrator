package com.sysco.uomdesktop.client

import com.sysco.uomorchestrator.api.customer.AccountRepresentation
import com.sysco.uomorchestrator.client.IIBClient
import com.sysco.uomorchestrator.client.IIBClientRequests
import com.sysco.uomorchestrator.client.iiborder.response.IIBOrderSubmitResponse
import com.sysco.uomorchestrator.core.model.Order
import com.sysco.uomorchestrator.exceptionmapper.GatewayTimeoutException
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.RESTClient
import org.apache.http.conn.HttpHostConnectException
import org.joda.time.DateTime
import org.joda.time.LocalDate
import spock.lang.Specification

import static com.sysco.uomdesktop.support.mothers.AccountMother.anAccountRepresentation
import static com.sysco.uomdesktop.support.mothers.OrderMother.anOrder
import static org.joda.time.DateTimeZone.UTC
import static org.joda.time.LocalDate.now

class IIBClientTest extends Specification {
    private static final Map SUBMIT_RESPONSE_DATA = [Acknowledgement: []]

    private HttpResponseDecorator submitOrderHttpResponse
    private RESTClient iibRestClient
    private IIBClientRequests iibClientRequests

    private IIBClient iibClient
    private AccountRepresentation account

    void setup() {
        submitOrderHttpResponse = Stub(HttpResponseDecorator) { getData() >> SUBMIT_RESPONSE_DATA }

        iibRestClient = Mock(RESTClient)
        iibClientRequests = Stub(IIBClientRequests)

        iibClient = new IIBClient(iibRestClient, iibClientRequests)
        account = anAccountRepresentation()
    }

    void 'submits an order to IIB'() {
        given:
        DateTime submitDate = new DateTime()
        Order order = anOrder()
        LocalDate deliveryDate = now(UTC)

        Map submitOrderRequestBody = Stub(Map)
        iibClientRequests.submitOrder(order, submitDate, deliveryDate, account) >> submitOrderRequestBody

        when:
        iibClient.submitOrder(order, submitDate, deliveryDate, account)

        then:
        1 * iibRestClient.post([
            path: '/services/ordersubmit',
            body: submitOrderRequestBody,
            requestContentType: 'application/json']
        ) >> submitOrderHttpResponse
    }

    void 'gives the IIB response back'() {
        given:
        iibRestClient.post(_ as Map) >> submitOrderHttpResponse

        when:
        IIBOrderSubmitResponse response = iibClient.submitOrder(anOrder(), new DateTime(), now(UTC), account)

        then:
        response == new IIBOrderSubmitResponse(SUBMIT_RESPONSE_DATA)
    }

    void 'throws an exception when unable to connect to IIB'() {
        given:
        iibRestClient.post(_ as Map) >> { throw new HttpHostConnectException(null, null) }

        when:
        iibClient.submitOrder(anOrder(), new DateTime(), now(UTC), account)

        then:
        GatewayTimeoutException exception = thrown(GatewayTimeoutException)
        exception.message == 'Unable to submit order to IIB'
    }
}
