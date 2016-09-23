package com.sysco.uomdesktop.dao.support

import org.skife.jdbi.v2.sqlobject.Bind
import org.skife.jdbi.v2.sqlobject.SqlUpdate

import java.sql.Time

interface TestOnlyAccountDao {
    @SqlUpdate("""
        insert into uom.account (id, delivery_calendar, cutoff_time, opco, customer_number)
                         values (:id, CAST('{}' as jsonb), '12:00:00', '010', '1234567')
    """)
    void insert(@Bind('id') UUID id)

    @SqlUpdate("""
        insert into uom.account (id, delivery_calendar, cutoff_time, opco, customer_number, customer_name)
                         values (:id, CAST(:deliveryCalendar as jsonb), :cutoffTime, :opco, :customerNumber, :customerName)
    """)
    void insertFull(@Bind('id') UUID id, @Bind('deliveryCalendar') String deliveryCalendar,
                    @Bind('cutoffTime') Time cutoffTime, @Bind('opco') String opco,
                    @Bind('customerNumber') String customerNumber, @Bind('customerName') String customerName)
}
