package com.sysco.uomorchestrator.core.validator

import com.sysco.uomorchestrator.core.model.LineItem
import com.sysco.uomorchestrator.core.model.LineItem

import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext

class OrderMaximumPriceValidator implements ConstraintValidator<MustNotExceedMaximumPrice, List<LineItem>> {
    def MAX_TOTAL_PRICE = Float.valueOf("10000000");

    @Override
    void initialize(MustNotExceedMaximumPrice constraintAnnotation) {

    }

    @Override
    boolean isValid(List<LineItem> items, ConstraintValidatorContext context) {
        def totalPrice = items.sum { it.price * it.quantity }
        return totalPrice < MAX_TOTAL_PRICE;
    }
}
