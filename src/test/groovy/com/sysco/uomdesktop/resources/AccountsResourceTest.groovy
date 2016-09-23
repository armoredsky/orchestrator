package com.sysco.uomdesktop.resources

import com.sysco.uomorchestrator.api.customer.AccountRepresentation
import com.sysco.uomorchestrator.core.service.AccountService
import com.sysco.uomorchestrator.resources.AccountsResource
import io.dropwizard.testing.junit.ResourceTestRule
import org.junit.Rule
import spock.lang.Specification

import javax.ws.rs.core.GenericType
import javax.ws.rs.core.Response

import static com.sysco.uomdesktop.support.mothers.AccountMother.anAccountRepresentation

class AccountsResourceTest extends Specification {
    private final AccountService accountService = Mock(AccountService)

    @Rule
    ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(new AccountsResource(accountService))
            .build()

    void '#get: successfully gets an account by id'() {
        given:
        UUID accountId = UUID.randomUUID()
        AccountRepresentation accountRepresentation = anAccountRepresentation(id: accountId)

        when:
        Response response = resources.client()
                .target("/accounts/${accountId}")
                .request()
                .get()

        then:
        1 * accountService.getById(accountId) >> accountRepresentation

        response.getStatusInfo() == Response.Status.OK
        response.readEntity(new GenericType<AccountRepresentation>(){}) == accountRepresentation
    }
}
