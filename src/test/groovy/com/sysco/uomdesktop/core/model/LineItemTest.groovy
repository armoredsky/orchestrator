package com.sysco.uomdesktop.core.model

import com.sysco.uomorchestrator.api.common.ProductRepresentation
import com.sysco.uomorchestrator.api.order.LineItemRepresentation
import com.sysco.uomorchestrator.core.model.LineItem
import spock.lang.Specification

import static com.sysco.uomdesktop.support.mothers.LineItemMother.aLineItem
import static com.sysco.uomdesktop.support.mothers.LineItemMother.aLineItemRepresentation
import static com.sysco.uomdesktop.support.mothers.ProductMother.aProduct
import static com.sysco.uomdesktop.support.mothers.ProductMother.aProductRepresentation
import static java.util.UUID.randomUUID

class LineItemTest extends Specification {

    void 'creates a LineItem from a LineItemRepresentation'() {
        given:
        UUID id = randomUUID()
        ProductRepresentation productRepresentation = aProductRepresentation()
        LineItemRepresentation lineItemRepresentation = aLineItemRepresentation(
            id: id,
            quantity: 6,
            price: '12.34',
            product: productRepresentation
        )

        when:
        LineItem lineItem = new LineItem(lineItemRepresentation)

        then:
        lineItem == aLineItem(
            id: id,
            quantity: 6,
            price: Float.valueOf('12.34'),
            product: aProduct(
                id: UUID.fromString(productRepresentation.id),
                name: productRepresentation.name,
                supc: productRepresentation.supc,
                brand: productRepresentation.brand,
                pack: productRepresentation.pack,
                size: productRepresentation.size
            )
        )
    }

    void 'defaults the id to a randomUUID when the representation did not have one'() {
        given:
        LineItemRepresentation lineItemRepresentation = aLineItemRepresentation(id: null)

        when:
        LineItem lineItem = new LineItem(lineItemRepresentation)

        then:
        lineItem.id != null
    }

    void 'defaults the price to 0'() {
        given:
        LineItemRepresentation lineItemRepresentation = aLineItemRepresentation(price: null)

        when:
        LineItem lineItem = new LineItem(lineItemRepresentation)

        then:
        lineItem.price == 0.0 as float
    }
}
