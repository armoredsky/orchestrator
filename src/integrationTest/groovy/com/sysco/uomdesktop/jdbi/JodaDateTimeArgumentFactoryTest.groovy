package com.sysco.uomdesktop.jdbi

import com.sysco.uomdesktop.support.IntegrationTestBase
import com.sysco.uomdesktop.dao.support.ArgumentFactoryTestDao
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.format.DateTimeFormat

class JodaDateTimeArgumentFactoryTest extends IntegrationTestBase {
    ArgumentFactoryTestDao argumentFactoryTestDao

    void setup() {
        argumentFactoryTestDao = dbi.onDemand(ArgumentFactoryTestDao)
        dbi.open().execute("create table date_times (id integer, timestamp timestamp with time zone)")
    }

    void 'inserts a joda date time as a timestamp'() {
        given:
        DateTime insertedDateTime = new DateTime(2016, 9, 8, 0, 0).withZone(DateTimeZone.UTC)

        when:
        int numberInserted = argumentFactoryTestDao.insertDateTime(1, insertedDateTime)

        and:
        String retrievedDateTime = argumentFactoryTestDao.findDateTime(1)

        then:
        numberInserted == 1
        DateTime.parse(retrievedDateTime, DateTimeFormat.forPattern("YYYY-MM-dd HH:mm:ssZ")).withZone(DateTimeZone.UTC) == insertedDateTime
    }

    void cleanup() {
        dbi.open().execute("drop table date_times")
    }

}
