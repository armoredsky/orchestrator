package com.sysco.uomorchestrator.core.model

import com.sysco.uomorchestrator.api.common.ProductRepresentation
import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode
class Product {
    UUID id
    String name
    String supc
    String brand
    String pack
    String size

    Product() { }

    Product(ProductRepresentation productResource) {
        this.id = productResource.id ? UUID.fromString(productResource.id) : null
        this.name = productResource.name
        this.supc = productResource.supc
        this.brand = productResource.brand
        this.pack = productResource.pack
        this.size = productResource.size
    }
}
