package com.sysco.uomdesktop.core.model

import com.sysco.uomorchestrator.api.common.ProductRepresentation
import com.sysco.uomorchestrator.core.model.Product
import spock.lang.Specification

class ProductTest extends Specification {
    void 'should build a Product from a ProductResource'() {
        given:
        UUID productId = UUID.randomUUID()
        ProductRepresentation representation = new ProductRepresentation(
            id: productId.toString(),
            name: "someName",
            supc: "1234",
            brand: "heinz",
            pack: "3",
            size: "2"
        )

        when:
        Product product = new Product(representation)

        then:
        product.id == productId
    }
}
