package com.sysco.uomdesktop.support

import com.sysco.uomorchestrator.repository.mapper.UUIDMapper
import org.skife.jdbi.v2.sqlobject.Bind
import org.skife.jdbi.v2.sqlobject.SqlQuery
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper

interface TestOnlyAccountDao {
    @RegisterMapper(UUIDMapper)
    @SqlQuery("""
        insert into uom.account (id, delivery_calendar, cutoff_time, opco, customer_number, customer_name)
                         values (uuid_generate_v4(), CAST('{"Wednesday":0}' AS jsonb), '12:00:00.000', '010',
                                 :customerNumber, 'Functional Test Customer')
        returning id
    """)
    UUID insert(@Bind('customerNumber') String customerNumber)
}
