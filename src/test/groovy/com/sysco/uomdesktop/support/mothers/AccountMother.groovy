package com.sysco.uomdesktop.support.mothers

import com.sysco.uomorchestrator.api.customer.AccountRepresentation
import com.sysco.uomorchestrator.core.model.Account
import org.joda.time.Days
import org.joda.time.LocalTime

import static java.util.UUID.randomUUID

class AccountMother {
    private static final Map defaultProperties = [id: randomUUID(), opCo: "010", customerNumber: "000026"]
    private static final Map deliveryCalendar = ['Friday': Days.ONE, 'Saturday': Days.ZERO]
    private static final LocalTime FIVE_PM = new LocalTime(12 + 5, 0, 0)

    static Account anAccount(Map overrides = [:]) {
        Map accountProperties = defaultProperties + [deliveryCalendar: deliveryCalendar, cutoffTime: FIVE_PM]
        new Account(accountProperties + overrides)
    }

    static AccountRepresentation anAccountRepresentation(Map overrides = [:]) {
        new AccountRepresentation(defaultProperties + overrides)
    }
}
