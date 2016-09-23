package com.sysco.uomorchestrator.core.model

import com.sysco.uomorchestrator.api.product.ProductListRepresentation
import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode
class ProductList {
    UUID id
    String name
    List<Product> products = []
    UUID accountId

    ProductList() { }

    ProductList(ProductListRepresentation productListRepresentation) {
        this.name = productListRepresentation.name
        this.products = productListRepresentation.products.collect { new Product(it) }
    }

    ProductList withProducts(List<Product> products) {
        this.products = products
        this
    }

    boolean isEmpty() {
        products.isEmpty()
    }
}
