package com.sysco.uomdesktop.dao

import com.sysco.uomorchestrator.core.model.Account
import com.sysco.uomdesktop.dao.support.TestOnlyAccountDao
import com.sysco.uomorchestrator.repository.dao.AccountDao
import com.sysco.uomdesktop.support.IntegrationTestBase

import java.sql.Time

class AccountDaoTest extends IntegrationTestBase {
    private AccountDao accountDao
    private TestOnlyAccountDao testOnlyAccountDao

    void setup() {
        accountDao = dbi.onDemand(AccountDao)
        testOnlyAccountDao = dbi.onDemand(TestOnlyAccountDao)
    }

    void 'gets an account by id'() {
        given:
        UUID id = UUID.randomUUID()

        and:
        testOnlyAccountDao.insert(id)

        when:
        Account account = accountDao.findOne(id)

        then:
        account != null
        account.id == id
    }

    void 'gets an account by customer number'() {
        given:
        UUID id = UUID.randomUUID()
        String customerNumber = '1234567'
        String customerName = 'A name'

        and:
        testOnlyAccountDao.insertFull(id, '{"Monday": 0}', Time.valueOf('12:00:00'), '010', customerNumber, customerName)

        when:
        Account account = accountDao.findOneByCustomerNumber(customerNumber)

        then:
        account != null
        account.id == id
        account.customerNumber == customerNumber
        account.customerName == customerName
    }
}
