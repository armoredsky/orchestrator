package com.sysco.uomorchestrator.core.service

import com.google.inject.Inject
import com.sysco.uomdesktop.api.customer.AccountRepresentation
import com.sysco.uomdesktop.core.model.Account
import com.sysco.uomdesktop.repository.dao.AccountDao
import com.sysco.uomorchestrator.api.customer.AccountRepresentation
import org.joda.time.DateTimeZone
import org.joda.time.LocalDate

class AccountService {
    private static final DateTimeZone CENTRAL = DateTimeZone.forID('America/Chicago')

    private final AccountDao accountDao
    private final CalculatesDeliveryDates calculatesDeliveryDates

    @Inject
    AccountService(AccountDao accountDao, com.sysco.uomdesktop.core.service.CalculatesDeliveryDates calculatesDeliveryDates) {
        this.accountDao = accountDao
        this.calculatesDeliveryDates = calculatesDeliveryDates
    }

    AccountRepresentation getById(UUID accountId) {
        Account account = accountDao.findOne(accountId)
        LocalDate deliveryDate = calculatesDeliveryDates.nextAvailable(account.deliveryCalendar, account.cutoffTime, CENTRAL)

        new AccountRepresentation(
            id: accountId,
            nextAvailableDeliveryDate: deliveryDate.toString("yyyy-MM-dd"),
            opCo: account.opCo,
            customerNumber: account.customerNumber,
            customerName: account.customerName
        )
    }

    AccountRepresentation getByCustomerNumber(String customerNumber) {
        Account account = accountDao.findOneByCustomerNumber(customerNumber)
        LocalDate deliveryDate = calculatesDeliveryDates.nextAvailable(account.deliveryCalendar, account.cutoffTime, CENTRAL)

        new AccountRepresentation(
            id: account.id,
            nextAvailableDeliveryDate: deliveryDate.toString("yyyy-MM-dd"),
            opCo: account.opCo,
            customerNumber: account.customerNumber,
            customerName: account.customerName
        )
    }

    LocalDate getNextAvailableDeliveryDate(UUID accountId) {
        Account account = accountDao.findOne(accountId)
        calculatesDeliveryDates.nextAvailable(account.deliveryCalendar, account.cutoffTime, CENTRAL)
    }
}
