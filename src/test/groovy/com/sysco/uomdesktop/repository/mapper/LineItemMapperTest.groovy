package com.sysco.uomdesktop.repository.mapper

import com.sysco.uomorchestrator.core.model.LineItem
import com.sysco.uomorchestrator.core.model.Product
import com.sysco.uomorchestrator.repository.mapper.LineItemMapper
import spock.lang.Specification

import java.sql.ResultSet

class LineItemMapperTest extends Specification {
    LineItemMapper lineItemMapper
    ResultSet mockResultSet

    void setup() {
        lineItemMapper = new LineItemMapper()
        mockResultSet = Mock(ResultSet)
    }

    void 'should create a line item from a result set'() {
        given:
        UUID lineItemId = UUID.randomUUID()
        UUID productId = UUID.randomUUID()
        LineItem expectedLineItem = new LineItem(
            id: lineItemId,
            quantity: 3,
            price: 12.99,
            product: new Product(id: productId)
        )
        mockResultSet.getObject('id', UUID) >> lineItemId
        mockResultSet.getInt('quantity') >> expectedLineItem.quantity
        mockResultSet.getFloat('price') >> expectedLineItem.price
        mockResultSet.getObject('product_id', UUID) >> productId

        when:
        LineItem lineItem = lineItemMapper.map(0, mockResultSet, null)

        then:
        lineItem == expectedLineItem
    }
}
