package com.sysco.uomdesktop.repository.mapper

import com.sysco.uomorchestrator.core.model.ProductList
import com.sysco.uomorchestrator.repository.mapper.ProductListMapper
import spock.lang.Specification

import java.sql.ResultSet

class ProductListMapperTest extends Specification {
    ProductListMapper productListMapper
    ResultSet mockResultSet

    void setup() {
        productListMapper = new ProductListMapper()
        mockResultSet = Mock(ResultSet)
    }

    void 'should create a product list from a result set'() {
        given:
        UUID productListId = UUID.randomUUID()
        ProductList expectedProductList = new ProductList(id: productListId, name: 'list name')
        mockResultSet.getObject('id', UUID) >> productListId
        mockResultSet.getString('name') >> expectedProductList.name

        when:
        ProductList productList = productListMapper.map(0, mockResultSet, null)

        then:
        productList == expectedProductList
    }
}
