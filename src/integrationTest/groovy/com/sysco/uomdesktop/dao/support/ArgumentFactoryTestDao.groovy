package com.sysco.uomdesktop.dao.support

import com.sysco.uomorchestrator.repository.jdbi.JodaLocalDateArgumentFactory
import com.sysco.uomorchestrator.repository.jdbi.JodaDateTimeArgumentFactory
import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.skife.jdbi.v2.sqlobject.Bind
import org.skife.jdbi.v2.sqlobject.SqlQuery
import org.skife.jdbi.v2.sqlobject.SqlUpdate
import org.skife.jdbi.v2.sqlobject.customizers.RegisterArgumentFactory

@RegisterArgumentFactory([JodaDateTimeArgumentFactory, JodaLocalDateArgumentFactory])
interface ArgumentFactoryTestDao {
    @SqlUpdate("insert into date_times (id, timestamp) values (:id, :timestamp)")
    int insertDateTime(@Bind('id') int id, @Bind('timestamp') DateTime timestamp)

    @SqlUpdate("insert into local_dates (id, local_date) values (:id, :date)")
    int insertLocalDate(@Bind('id') int id, @Bind('date') LocalDate date)

    @SqlQuery("select timestamp from date_times where id=:id")
    String findDateTime(@Bind("id") int id)

    @SqlQuery("select local_date from local_dates where id=:id")
    String findLocalDate(@Bind("id") int id)
}
