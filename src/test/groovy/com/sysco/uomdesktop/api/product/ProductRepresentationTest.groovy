package com.sysco.uomdesktop.api.product

import com.sysco.uomorchestrator.core.model.Product
import com.sysco.uomorchestrator.api.common.ProductRepresentation
import spock.lang.Specification

import static java.util.UUID.randomUUID

class ProductRepresentationTest extends Specification{

    UUID uuid = randomUUID()

    void 'ProductResource should map default fields from Product domain'() {
        given:
        Product product = new Product(id: uuid)

        ProductRepresentation expected = new ProductRepresentation()
        expected.id = uuid as String

        when:
        ProductRepresentation actual = new ProductRepresentation(product)

        then:
        expected == actual

    }

    void 'ProductResource should map all fields from Product domain'() {
        given:
        Product product = new Product(id:uuid, name: "name", supc: "supc", brand: "brand", pack: "pack", size: 1 )

        ProductRepresentation expected = new ProductRepresentation()
        expected.id = uuid as String
        expected.name = "name"
        expected.supc = "supc"
        expected.brand = "brand"
        expected.pack = "pack"
        expected.size = 1

        when:
        ProductRepresentation actual = new ProductRepresentation(product)

        then:
        expected == actual

    }
}
