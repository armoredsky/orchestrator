package com.sysco.uomdesktop.core.service

import com.sysco.uomorchestrator.api.customer.AccountRepresentation
import com.sysco.uomorchestrator.core.model.Account
import com.sysco.uomorchestrator.core.service.AccountService
import com.sysco.uomorchestrator.core.service.CalculatesDeliveryDates
import com.sysco.uomorchestrator.repository.dao.AccountDao
import org.joda.time.DateTimeZone
import org.joda.time.LocalDate
import spock.lang.Specification

import static com.sysco.uomdesktop.support.mothers.AccountMother.anAccount

class AccountServiceTest extends Specification {
    private static final DateTimeZone CENTRAL = DateTimeZone.forID('America/Chicago')

    private AccountDao mockAccountDao
    private CalculatesDeliveryDates mockCalculatesDeliveryDates
    private AccountService accountService

    void setup() {
        mockAccountDao = Mock(AccountDao)
        mockCalculatesDeliveryDates = Mock(CalculatesDeliveryDates)

        accountService = new AccountService(mockAccountDao, mockCalculatesDeliveryDates)
    }

    void 'should get an account by id'() {
        given:
        UUID accountId = UUID.randomUUID()
        Account account = anAccount(id: accountId, opCo: "319", customerNumber: "8675309", customerName: 'foo')

        and:
        mockAccountDao.findOne(accountId) >> account
        mockCalculatesDeliveryDates.nextAvailable(account.deliveryCalendar, account.cutoffTime, CENTRAL) >> new LocalDate(2016, 4, 2)

        when:
        AccountRepresentation accountRepresentation = accountService.getById(accountId)

        then:
        accountRepresentation.id == accountId
        accountRepresentation.nextAvailableDeliveryDate == "2016-04-02"
        accountRepresentation.opCo == "319"
        accountRepresentation.customerNumber == "8675309"
        accountRepresentation.customerName == 'foo'
    }

    void 'should get an account by customer number'() {
        given:
        UUID accountId = UUID.randomUUID()
        String customerNumber = "8675309"
        Account account = anAccount(id: accountId, opCo: "319", customerNumber: customerNumber, customerName: 'name')

        and:
        mockAccountDao.findOneByCustomerNumber(customerNumber) >> account
        mockCalculatesDeliveryDates.nextAvailable(account.deliveryCalendar, account.cutoffTime, CENTRAL) >> new LocalDate(2016, 4, 2)

        when:
        AccountRepresentation accountRepresentation = accountService.getByCustomerNumber(customerNumber)

        then:
        accountRepresentation.id == accountId
        accountRepresentation.nextAvailableDeliveryDate == "2016-04-02"
        accountRepresentation.opCo == "319"
        accountRepresentation.customerNumber == customerNumber
        accountRepresentation.customerName == 'name'
    }

    void 'should get next available delivery date for an account'() {
        given:
        UUID accountId = UUID.randomUUID()
        Account account = anAccount(id: accountId, opCo: "319", customerNumber: "8675309")
        LocalDate nextAvailableDeliveryDate = LocalDate.now()

        and:
        mockAccountDao.findOne(accountId) >> account
        mockCalculatesDeliveryDates.nextAvailable(account.deliveryCalendar, account.cutoffTime, CENTRAL) >> nextAvailableDeliveryDate

        when:
        LocalDate deliveryDate = accountService.getNextAvailableDeliveryDate(accountId)

        then:
        deliveryDate == nextAvailableDeliveryDate
    }
}
