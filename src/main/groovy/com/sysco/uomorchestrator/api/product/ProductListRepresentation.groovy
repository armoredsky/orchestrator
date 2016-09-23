package com.sysco.uomorchestrator.api.product

import com.sysco.uomorchestrator.api.common.ProductRepresentation
import com.sysco.uomorchestrator.core.model.ProductList
import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode
class ProductListRepresentation {
    String id
    String name
    List<ProductRepresentation> products

    ProductListRepresentation() { }

    ProductListRepresentation(ProductList productList) {
        this.id = productList.id.toString()
        this.name = productList.name
        this.products = productList.products.collect { new ProductRepresentation(it) }
    }
}
