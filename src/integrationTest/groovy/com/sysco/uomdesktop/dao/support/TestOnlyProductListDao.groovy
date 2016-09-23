package com.sysco.uomdesktop.dao.support

import com.sysco.uomorchestrator.core.model.Product
import com.sysco.uomorchestrator.core.model.ProductList
import org.skife.jdbi.v2.sqlobject.Bind
import org.skife.jdbi.v2.sqlobject.BindBean
import org.skife.jdbi.v2.sqlobject.SqlBatch
import org.skife.jdbi.v2.sqlobject.SqlUpdate

abstract class TestOnlyProductListDao {
    void insert(ProductList productList) {
        insertProductList(productList)
        insertProducts(productList.products)
        insertProductListLineItems(productList.products*.id, productList.id, productList.products*.supc)
    }

    @SqlUpdate("""
        insert into uom.product_list (id, account_id, name)
                              values (:productList.id, :productList.accountId, :productList.name)
    """)
    abstract void insertProductList(@BindBean('productList') ProductList productList)

    @SqlBatch("""
        insert into uom.product (id, name, supc, brand, pack, size)
                         values (:product.id, :product.name, :product.supc, :product.brand, :product.pack, :product.size)
    """)
    abstract void insertProducts(@BindBean('product') List<Product> products)

    @SqlBatch("""
        insert into uom.product_list_line_item (id, product_id, product_list_id, supc)
                                        values (uuid_generate_v4(), :productId, :productListId, :supc)
    """)
    abstract void insertProductListLineItems(@Bind('productId') List<UUID> productIds,
                                             @Bind('productListId') UUID productListId,
                                             @Bind('supc') List<String> supcs)
}
