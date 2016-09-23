package com.sysco.uomdesktop.builder

import com.sysco.uomorchestrator.core.model.LineItem
import com.sysco.uomorchestrator.core.model.Product

import static java.util.UUID.randomUUID

class LineItemBuilder {

    UUID id = randomUUID()
    int quantity = 4
    float price = 3.33
    Product product = new ProductBuilder().build()

    LineItem build() {
        new LineItem(id: id, quantity: quantity, price: price, product: product)
    }

    LineItemBuilder withQuantity(int quantity) {
        this.quantity = quantity
        this
    }

    LineItemBuilder withId(UUID id) {
        this.id = id
        this
    }
}
