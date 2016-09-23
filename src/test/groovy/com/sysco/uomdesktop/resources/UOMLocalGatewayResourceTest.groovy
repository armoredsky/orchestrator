package com.sysco.uomdesktop.resources

import com.sysco.uomorchestrator.api.uomlocal.UOMLocalAccountRepresentation
import com.sysco.uomorchestrator.core.service.UOMLocalService
import com.sysco.uomorchestrator.resources.UOMLocalGatewayResource
import io.dropwizard.testing.junit.ResourceTestRule
import org.junit.Rule
import spock.lang.Specification

import javax.ws.rs.core.GenericType
import javax.ws.rs.core.Response

class UOMLocalGatewayResourceTest extends Specification {
    private final UOMLocalService uomLocalService = Mock(UOMLocalService)

    @Rule
    ResourceTestRule resources = ResourceTestRule.builder()
        .addResource(new UOMLocalGatewayResource(uomLocalService))
        .build()

    void '#loadAccount: responds with the product lists for an account'() {
        given:
        UOMLocalAccountRepresentation account = new UOMLocalAccountRepresentation()

        when:
        Response response = resources.client()
            .target("/uomlocal-gateway/accounts?customerNumber=1234567")
            .request()
            .get()

        then:
        1 * uomLocalService.loadAccount('1234567') >> account

        response.getStatusInfo() == Response.Status.OK
        response.readEntity(new GenericType<UOMLocalAccountRepresentation>(){}) == account
    }
}
