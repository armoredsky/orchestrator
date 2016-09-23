package com.sysco.uomorchestrator.api.common

import com.fasterxml.jackson.annotation.JsonInclude
import com.sysco.uomorchestrator.core.model.Product
import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode
class ProductRepresentation {
    String id
    String name
    String supc
    String brand
    String pack
    String size
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String price

    ProductRepresentation() { }

    ProductRepresentation(Product product) {
        this.id = product.id.toString()
        this.name = product.name
        this.supc = product.supc
        this.brand = product.brand
        this.pack = product.pack
        this.size = product.size
    }
}

