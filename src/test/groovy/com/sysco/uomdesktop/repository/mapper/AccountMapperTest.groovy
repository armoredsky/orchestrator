package com.sysco.uomdesktop.repository.mapper

import com.sysco.uomorchestrator.core.model.Account
import com.sysco.uomorchestrator.repository.mapper.AccountMapper
import org.joda.time.Days
import org.joda.time.LocalTime
import spock.lang.Specification

import java.sql.ResultSet
import java.sql.Time

import static com.sysco.uomdesktop.support.mothers.AccountMother.anAccount

class AccountMapperTest extends Specification {
    AccountMapper accountMapper
    ResultSet mockResultSet

    void setup() {
        accountMapper = new AccountMapper()
        mockResultSet = Mock(ResultSet)
    }

    void 'should create an account from a result set'() {
        given:
        UUID id = UUID.randomUUID()
        mockResultSet.getObject('id', UUID) >> id
        mockResultSet.getTime('cutoff_time') >> new Time(17, 0, 0)
        mockResultSet.getString('delivery_calendar') >> '{"Friday": 1, "Saturday": 0}'
        mockResultSet.getString('opco') >> '319'
        mockResultSet.getString('customer_number') >> '8675309'
        mockResultSet.getString('customer_name') >> 'a name'

        when:
        Account account = accountMapper.map(0, mockResultSet, null)

        then:
        account == anAccount(
                id: id,
                cutoffTime: new LocalTime(17, 0, 0),
                deliveryCalendar: ['Friday': Days.ONE, 'Saturday': Days.ZERO],
                opCo: '319',
                customerNumber: '8675309',
                customerName: 'a name'
        )
    }
}
