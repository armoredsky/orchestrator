package com.sysco.uomdesktop.support

import org.skife.jdbi.v2.DBI
import org.skife.jdbi.v2.Handle
import org.skife.jdbi.v2.TransactionCallback
import org.skife.jdbi.v2.TransactionStatus

class DbSupport {

    public static final DBI DB = new DBI(
        "jdbc:postgresql://${System.getenv('DATABASE_HOST')}/${System.getenv('DATABASE')}",
        'uom',
        ''
    )

    static truncateTables() {
        List<String> tables = ['account',
                               'order',
                               'order_line_item',
                               'product',
                               'product_list',
                               'product_list_line_item']

        tables.each { tableName ->
            String cleanDatabaseStatement = "delete from uom.${tableName}"
            DB.inTransaction(new TransactionCallback<Integer>() {
                @Override
                Integer inTransaction(Handle conn, TransactionStatus status) throws Exception {
                    conn.createStatement(cleanDatabaseStatement).execute()
                }
            })
        }
    }
}
