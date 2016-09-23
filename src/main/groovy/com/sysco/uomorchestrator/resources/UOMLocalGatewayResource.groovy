package com.sysco.uomorchestrator.resources

import com.google.inject.Inject
import com.sysco.uomorchestrator.core.service.UOMLocalService

import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path('/uomlocal-gateway')
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class UOMLocalGatewayResource {

    private final UOMLocalService uomLocalService

    @Inject
    UOMLocalGatewayResource(UOMLocalService uomLocalService) {
        this.uomLocalService = uomLocalService
    }

    @GET
    @Path('/accounts')
    def loadAccount(@QueryParam('customerNumber') String customerNumber) {
        Response.ok()
            .entity(uomLocalService.loadAccount(customerNumber))
            .build()
    }
}
