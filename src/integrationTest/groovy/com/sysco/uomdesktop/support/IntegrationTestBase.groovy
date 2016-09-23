package com.sysco.uomdesktop.support

import com.sysco.uomdesktop.dao.support.TestOnlyAccountDao
import org.skife.jdbi.v2.DBI
import org.skife.jdbi.v2.Handle
import org.skife.jdbi.v2.TransactionCallback
import org.skife.jdbi.v2.TransactionStatus
import spock.lang.Specification

abstract class IntegrationTestBase extends Specification {
    def db = [
        host  : "jdbc:postgresql://${System.getenv('DATABASE_HOST') ?: "localhost"}/${System.getenv('DATABASE') ?: "uom_test"}",
        user  : 'uom',
        pass  : '',
        driver: 'org.postgresql.Driver'
    ]
    final DBI dbi = new DBI(db.host, db.user, db.pass)

    void setup() {
        emptyAllTables()
    }

    private void emptyAllTables() {
        List<String> tablesInDatabase = ['account',
                                         'order',
                                         'order_line_item',
                                         'product',
                                         'product_list',
                                         'product_list_line_item']

        tablesInDatabase.each { tableName ->
            String cleanDatabaseStatement = "delete from uom.${tableName}"
            dbi.inTransaction(new TransactionCallback<Integer>() {
                @Override
                Integer inTransaction(Handle conn, TransactionStatus status) throws Exception {
                    conn.createStatement(cleanDatabaseStatement).execute()
                }
            })
        }
    }

    protected List<Map<String, Object>> executeSqlQuery(String query) {
        dbi.inTransaction(new TransactionCallback() {
            @Override
            Object inTransaction(Handle conn, TransactionStatus status) throws Exception {
                conn.select(query)
            }
        })
    }

    protected UUID anAccountExists() {
        UUID id = UUID.randomUUID()
        TestOnlyAccountDao testOnlyAccountDao = dbi.onDemand(TestOnlyAccountDao)
        testOnlyAccountDao.insert(id)
        id
    }
}
