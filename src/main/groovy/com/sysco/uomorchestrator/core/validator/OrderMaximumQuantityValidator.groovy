package com.sysco.uomorchestrator.core.validator

import com.sysco.uomorchestrator.core.model.LineItem

import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext

class OrderMaximumQuantityValidator implements ConstraintValidator<MustNotExceedMaximumQuantity, List<LineItem>> {
    def MAX_NUMBER_OF_ITEMS = 10000;

    @Override
    void initialize(MustNotExceedMaximumQuantity constraintAnnotation) {

    }

    @Override
    boolean isValid(List<LineItem> lineItems, ConstraintValidatorContext context) {
        def totalItems = lineItems.sum { it.quantity }
        return totalItems < MAX_NUMBER_OF_ITEMS;
    }
}
