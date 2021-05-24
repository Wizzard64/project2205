package com.mastery.java.task.validator;

import com.mastery.java.task.dto.Gender;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class GenderValidator implements ConstraintValidator<ValidGender, String> {

    @Override
    public void initialize(ValidGender constraintAnnotation) {

    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        for (Gender gender : Gender.values())
            if (value.equals(gender.toString()))
                return true;
        return false;
    }
}
