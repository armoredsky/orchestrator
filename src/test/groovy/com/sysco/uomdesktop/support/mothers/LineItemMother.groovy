package com.sysco.uomdesktop.support.mothers

import com.sysco.uomorchestrator.api.order.LineItemRepresentation
import com.sysco.uomorchestrator.core.model.LineItem

import static ProductMother.aProduct
import static com.sysco.uomdesktop.support.mothers.ProductMother.aProductRepresentation
import static java.util.UUID.randomUUID

class LineItemMother {
    static LineItem aLineItem(Map overrides = [:]) {
        Map defaultProperties = [
            id: randomUUID(),
            quantity: 5,
            price: Float.valueOf('23.45'),
            product: aProduct()
        ]

        new LineItem(defaultProperties + overrides)
    }

    static LineItemRepresentation aLineItemRepresentation(Map overrides = [:]) {
        Map defaultProperties = [
            id: randomUUID(),
            quantity: 5,
            price: '23.45',
            product: aProductRepresentation()
        ]

        new LineItemRepresentation(defaultProperties + overrides)
    }
}
