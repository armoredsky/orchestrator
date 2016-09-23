package com.sysco.uomdesktop.core.validator

import com.sysco.uomorchestrator.core.model.LineItem
import com.sysco.uomorchestrator.core.validator.OrderMaximumQuantityValidator
import spock.lang.Specification

import static com.sysco.uomdesktop.support.mothers.LineItemMother.aLineItem
import static com.sysco.uomdesktop.support.mothers.ProductMother.aProduct

class OrderMaximumQuantityValidatorTest extends Specification {

    private final OrderMaximumQuantityValidator validator = new OrderMaximumQuantityValidator()

    void 'should return false when order has over 10,000 line items'() {
        given:
        LineItem lineItem = aLineItem(quantity: 10000, product: aProduct(supc: '0005678'))

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
