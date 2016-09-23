package com.sysco.uomdesktop.dao

import com.sysco.uomorchestrator.core.model.Product
import com.sysco.uomorchestrator.repository.dao.ProductDao
import com.sysco.uomdesktop.support.IntegrationTestBase

import static com.sysco.uomdesktop.dao.fixture.ProductFixtures.*

class ProductDaoTest extends IntegrationTestBase {
    ProductDao productDao
    final Product product = new Product(product1Values)

    void setup() {
        productDao = dbi.onDemand(ProductDao)
    }

    void 'should create a product and return its id'() {
        when:
        UUID insertedProductId = productDao.insert(product)

        then:
        executeSqlQuery('select * from product') == [
            product1Values + [id: insertedProductId ]
        ]
    }

    void 'should batch insert many products and return their ids' () {
        when:
        List<UUID> insertedIds = productDao.insertMany(products)

        then:
        insertedIds.size() == 2
        executeSqlQuery('select * from product') == [
            product1Values + [id: insertedIds[0]],
            product2Values + [id: insertedIds[1]]
        ]
    }

    void 'should retrieve a product by its id'() {
        given:
        UUID insertedProductId = productDao.insert(product)

        when:
        Product retrievedProduct = productDao.getById(insertedProductId)

        then:
        retrievedProduct == new Product(product1Values + [id: insertedProductId ] )
    }
}
