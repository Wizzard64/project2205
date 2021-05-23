package com.mastery.java.task.dto.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Constraint(validatedBy = IsGenderValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface IsGender {
    String message() default "Некоректный пол, коррктное значение - MALE/FEMALE";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
