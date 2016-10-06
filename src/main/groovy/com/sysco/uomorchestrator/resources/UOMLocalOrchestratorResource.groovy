package com.sysco.uomorchestrator.resources

import com.google.inject.Inject
import com.sysco.uomorchestrator.core.service.UOMLocalService

import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path('/uom-local')
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class UOMLocalOrchestratorResource {

    @Inject
    private final UOMLocalService uomLocalService

    @GET
    @Path('/accounts')
    def loadAccount(@QueryParam('customerNumber') String customerNumber) {
        Response.ok()
            .entity(uomLocalService.loadAccount(customerNumber))
            .build()
    }
}
