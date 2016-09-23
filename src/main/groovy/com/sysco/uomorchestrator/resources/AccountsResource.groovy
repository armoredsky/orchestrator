package com.sysco.uomorchestrator.resources

import com.google.inject.Inject
import com.sysco.uomdesktop.core.service.AccountService
import com.sysco.uomorchestrator.core.service.AccountService

import javax.ws.rs.*
import javax.ws.rs.core.MediaType

@Path('/accounts')
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class AccountsResource {

    private final AccountService accountService

    @Inject
    AccountsResource(AccountService accountService) {
        this.accountService = accountService
    }

    @GET
    @Path('/{id}')
    def get(@PathParam('id') UUID id) {
        accountService.getById(id)
    }
}

