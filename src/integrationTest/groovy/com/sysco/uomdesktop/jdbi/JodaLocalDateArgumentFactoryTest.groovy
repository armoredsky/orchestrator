package com.sysco.uomdesktop.jdbi

import com.sysco.uomdesktop.support.IntegrationTestBase
import com.sysco.uomdesktop.dao.support.ArgumentFactoryTestDao
import org.joda.time.LocalDate

class JodaLocalDateArgumentFactoryTest extends IntegrationTestBase {
    ArgumentFactoryTestDao argumentFactoryTestDao

    void setup(){
        argumentFactoryTestDao = dbi.onDemand(ArgumentFactoryTestDao)
        dbi.open().execute("create table local_dates (id integer, local_date date)")
    }

    void 'inserts a joda local date as a sql date'() {
        given:
        LocalDate insertedDate = new LocalDate(2016, 9, 10)

        when:
        int numberInserted = argumentFactoryTestDao.insertLocalDate(1, insertedDate)

        and:
        String retrievedDate = argumentFactoryTestDao.findLocalDate(1)

        then:
        numberInserted == 1
        retrievedDate == "2016-09-10"
    }

    void cleanup(){
        dbi.open().execute("drop table local_dates")
    }

}
