package com.sysco.uomorchestrator.core.model

import com.sysco.uomorchestrator.api.order.LineItemRepresentation
import groovy.transform.EqualsAndHashCode

import static java.util.UUID.randomUUID

@EqualsAndHashCode
class LineItem {
    UUID id
    int quantity
    float price
    Product product

    LineItem() {}

    LineItem(LineItemRepresentation lineItemRepresentation) {
        this.id = lineItemRepresentation.id ? lineItemRepresentation.id : randomUUID()
        this.quantity = lineItemRepresentation.quantity
        this.price = lineItemRepresentation.price ? Float.parseFloat(lineItemRepresentation.price) : 0.0
        this.product = new Product(lineItemRepresentation.product)
    }

    LineItem withProduct(Product product) {
        this.product = product
        this
    }
}
