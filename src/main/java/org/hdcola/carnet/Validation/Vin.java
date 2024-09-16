package org.hdcola.carnet.Validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = VinValidator.class)
public @interface Vin {

    String message();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
