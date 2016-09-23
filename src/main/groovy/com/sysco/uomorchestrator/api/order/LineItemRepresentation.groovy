package com.sysco.uomorchestrator.api.order

import com.sysco.uomorchestrator.core.model.LineItem
import com.sysco.uomorchestrator.api.common.ProductRepresentation
import com.sysco.uomorchestrator.api.common.ProductRepresentation
import com.sysco.uomorchestrator.core.model.LineItem
import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode
class LineItemRepresentation {
    UUID id
    int quantity
    String price
    ProductRepresentation product

    LineItemRepresentation() {}

    LineItemRepresentation(LineItem lineItem) {
        this.id = lineItem.id
        this.quantity = lineItem.quantity
        this.price = lineItem.price
        this.product = new ProductRepresentation(lineItem.product)
    }
}
