package com.sysco.uomorchestrator.core.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(FIELD)
@Retention(RUNTIME)
@Constraint(validatedBy = OrderMaximumQuantityValidator.class)
public @interface MustNotExceedMaximumQuantity {
    String message();

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default {};
}