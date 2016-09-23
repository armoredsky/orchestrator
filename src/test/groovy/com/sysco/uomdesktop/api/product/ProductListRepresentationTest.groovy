package com.sysco.uomdesktop.api.product

import com.sysco.uomorchestrator.api.product.ProductListRepresentation
import com.sysco.uomorchestrator.core.model.ProductList
import spock.lang.Specification

import static java.util.UUID.randomUUID

class ProductListRepresentationTest extends Specification {

    UUID uuid = randomUUID()

    void 'ProductListResource should map default fields from ProductList domain'() {
        given:
        ProductList productList = new ProductList(id: uuid)

        ProductListRepresentation expected = new ProductListRepresentation()
        expected.id = uuid as String
        expected.products = []

        when:
        ProductListRepresentation actual = new ProductListRepresentation(productList)

        then:
        expected == actual

    }

    void 'ProductListResource should map all fields from ProductList domain'() {
        given:
        ProductList product = new ProductList(id: uuid, name: "name", products: [])

        ProductListRepresentation expected = new ProductListRepresentation()
        expected.id = uuid as String
        expected.name = "name"
        expected.products = []

        when:
        ProductListRepresentation actual = new ProductListRepresentation(product)

        then:
        expected == actual

    }
}
