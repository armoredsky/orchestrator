package com.sysco.uomdesktop.builder

import com.sysco.uomorchestrator.core.model.Product

import static java.util.UUID.randomUUID

class ProductBuilder {

    UUID id = randomUUID()
    String name = "some product name"
    String supc = "12345"
    String brand = "brand name"
    String pack = "12"
    String size = "5"

    Product build() {
        new Product(id: id, name: name, supc: supc, brand: brand, pack: pack, size: size)
    }

}
