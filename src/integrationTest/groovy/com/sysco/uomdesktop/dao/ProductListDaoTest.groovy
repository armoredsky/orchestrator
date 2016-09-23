package com.sysco.uomdesktop.dao

import com.sysco.uomorchestrator.core.model.ProductList
import com.sysco.uomorchestrator.repository.dao.ProductListDao
import com.sysco.uomdesktop.support.IntegrationTestBase

import static com.sysco.uomdesktop.dao.fixture.ProductFixtures.productListValues
import static com.sysco.uomdesktop.support.mothers.ProductListMother.aProductList

class ProductListDaoTest extends IntegrationTestBase {
    ProductListDao productListDao

    void setup() {
        productListDao = dbi.onDemand(ProductListDao)
    }

    void 'should insert a product list into the db and return its id'() {
        given:
        executeSqlQuery('select * from product_list').size() == 0
        ProductList productList = new ProductList(productListValues)

        when:
        UUID insertedProductListId = productListDao.insert(productList)

        then:
        insertedProductListId instanceof UUID
        executeSqlQuery('select * from product_list').size() == 1
    }

    void 'should insert each product in the list into the db'() {
        given:
        executeSqlQuery('select * from product').size() == 0

        when:
        productListDao.insert(new ProductList(productListValues))

        then:
        executeSqlQuery('select * from product').size() == 2
    }

    void 'should get a product list by id'() {
        given:
        UUID insertedProductListId = productListDao.insert(new ProductList(productListValues))

        when:
        ProductList productList = productListDao.getById(insertedProductListId)
        List<String> productNames = productList.products*.name

        then:
        productList.id == insertedProductListId
        productList.name == productListValues.name
        productList.products.size() == productListValues.products.size()
        productNames.containsAll([productListValues.products[0].name,
                                  productListValues.products[1].name])
    }

    void 'should get an empty product list when no product list with that id exists'() {
        given:
        UUID nonexistentListId = UUID.randomUUID()

        when:
        ProductList productList = productListDao.getById(nonexistentListId)

        then:
        productList == null
    }

    void 'should delete all product lists'() {
        given:
        UUID insertedId = productListDao.insertProductList(aProductList(accountId: null))

        when:
        productListDao.deleteAll()

        and:
        ProductList productList = productListDao.getById(insertedId)

        then:
        productList == null
    }

    void 'should get all product lists by account id'() {
        given:
        UUID accountId = anAccountExists()
        UUID otherAccountId = anAccountExists()

        and:
        UUID productListOneId = productListDao.insert(aProductList([accountId: accountId]))
        UUID productListTwoId = productListDao.insert(aProductList([accountId: accountId]))
        productListDao.insert(aProductList([accountId: otherAccountId]))

        when:
        List<ProductList> productLists = productListDao.findAllByAccountId(accountId)

        then:
        productLists.size() == 2
        productLists*.id.containsAll(productListOneId, productListTwoId)
        !productLists[0].isEmpty()
        !productLists[1].isEmpty()
    }
}
