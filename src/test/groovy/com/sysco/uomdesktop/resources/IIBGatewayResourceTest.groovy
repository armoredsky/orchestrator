package com.sysco.uomdesktop.resources

import com.sysco.uomorchestrator.core.service.OrderService

import io.dropwizard.testing.junit.ResourceTestRule
import org.joda.time.LocalDate
import org.junit.Rule
import spock.lang.Specification

import javax.ws.rs.core.Response

import static com.sysco.uomdesktop.support.mothers.IIBOrderConfirmationMother.iibOrderConfirmation
import static javax.ws.rs.client.Entity.json

class IIBGatewayResourceTest extends Specification {

    OrderService orderService = Mock(OrderService)

    @Rule
    ResourceTestRule resources = ResourceTestRule.builder()
        .addResource(new IIBGatewayResource(orderService))
        .build();

    void 'confirms an order'() {
        given:
        Map orderConfirmation = iibOrderConfirmation(
            UOMOrderNo: '5555555',
            ERPOrderNumber: '00001234',
            DeliveryDate: '160907'
        )

        when:
        Response response = resources.client()
            .target("/iib-gateway/confirm-order")
            .request()
            .post(json(orderConfirmation))

        then:
        1 * orderService.confirm('5555555', '00001234', new LocalDate(2016, 9, 7))
        response.getStatusInfo() == Response.Status.OK
    }
}
