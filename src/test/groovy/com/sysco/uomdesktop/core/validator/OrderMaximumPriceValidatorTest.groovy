package com.sysco.uomdesktop.core.validator

import com.sysco.uomorchestrator.core.model.LineItem
import com.sysco.uomorchestrator.core.validator.OrderMaximumPriceValidator
import spock.lang.Specification

import static com.sysco.uomdesktop.support.mothers.LineItemMother.aLineItem
import static com.sysco.uomdesktop.support.mothers.ProductMother.aProduct

class OrderMaximumPriceValidatorTest extends Specification {

    private final OrderMaximumPriceValidator validator = new OrderMaximumPriceValidator()

    void 'should return false when order has a total price of over 9,999,999.99 with one item'() {
        given:
        LineItem lineItem = aLineItem(quantity: 1, product: aProduct(supc: '0005678'), price: Float.valueOf("10000000"))

        when:
        def isValid = validator.isValid([lineItem], null)

        then:
        !isValid
    }

    void 'should return false when order has a total price of over 9,999,999.99 with multiple items'() {
        given:
        LineItem lineItem = aLineItem(quantity: 100, product: aProduct(supc: '0005678'), price: Float.valueOf("100000"))

        when:
        def isValid = validator.isValid([lineItem], null)

        then:
        !isValid
    }

    void 'should return true when order is valid'() {
        given:
        LineItem lineItem = aLineItem()

        when:
        def isValid = validator.isValid([lineItem], null)

        then:
        isValid
    }
}
