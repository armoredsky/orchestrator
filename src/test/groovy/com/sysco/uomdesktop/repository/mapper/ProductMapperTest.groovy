package com.sysco.uomdesktop.repository.mapper

import com.sysco.uomorchestrator.core.model.Product
import com.sysco.uomorchestrator.repository.mapper.ProductMapper
import spock.lang.Specification

import java.sql.ResultSet

class ProductMapperTest extends Specification {
    ProductMapper productMapper
    ResultSet mockResultSet

    void setup() {
        productMapper = new ProductMapper()
        mockResultSet = Mock(ResultSet)
    }

    void 'should create a product from a result set'() {
        given:
        UUID productId = UUID.randomUUID()
        Product expectedProduct = new Product(
            id: productId,
            name: 'Apple',
            supc: "1234567",
            brand: "Foo Brand",
            pack: '32 oz',
            size: '12')
        mockResultSet.getObject('id', UUID) >> productId
        mockResultSet.getString('name') >> expectedProduct.name
        mockResultSet.getString('supc') >> expectedProduct.supc
        mockResultSet.getString('brand') >> expectedProduct.brand
        mockResultSet.getString('pack') >> expectedProduct.pack
        mockResultSet.getString('size') >> expectedProduct.size

        when:
        Product product = productMapper.map(0, mockResultSet, null)

        then:
        product == expectedProduct
    }
}
