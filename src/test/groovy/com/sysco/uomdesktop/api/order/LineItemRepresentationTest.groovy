package com.sysco.uomdesktop.api.order

import com.sysco.uomorchestrator.api.order.LineItemRepresentation
import com.sysco.uomorchestrator.core.model.LineItem
import com.sysco.uomorchestrator.core.model.Product
import spock.lang.Specification

import static com.sysco.uomdesktop.support.mothers.LineItemMother.aLineItem
import static com.sysco.uomdesktop.support.mothers.LineItemMother.aLineItemRepresentation
import static com.sysco.uomdesktop.support.mothers.ProductMother.aProduct
import static com.sysco.uomdesktop.support.mothers.ProductMother.aProductRepresentation
import static java.util.UUID.randomUUID

class LineItemRepresentationTest extends Specification {

    void 'LineItemResource should map fields from LineItem domain'() {
        given:
        UUID lineItemId = randomUUID()
        Product product = aProduct()
        LineItem lineItem = aLineItem(
            id: lineItemId,
            quantity: 6,
            price: 123.45,
            product: product
        )

        when:
        LineItemRepresentation actual = new LineItemRepresentation(lineItem)

        then:
        actual == aLineItemRepresentation(
            id: lineItemId,
            quantity: 6,
            price: 123.45,
            product: aProductRepresentation(
                id: product.id.toString(),
                name: product.name,
                supc: product.supc,
                brand: product.brand,
                pack: product.pack,
                size: product.size
            )
        )

    }
}
