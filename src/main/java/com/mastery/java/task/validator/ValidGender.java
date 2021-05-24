package com.mastery.java.task.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Constraint(validatedBy = GenderValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidGender {
    String message() default "Non-correct gender, correct value - MALE/FEMALE";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
