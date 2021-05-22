package com.mastery.java.task.dto.validator;

import com.mastery.java.task.dto.Gender;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class IsGenderValidator implements ConstraintValidator<IsGender, String> {

    @Override
    public void initialize(IsGender constraintAnnotation) {

    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        for (Gender gender: Gender.values())
            if(value.equals(gender.toString()))
                return true;
        return false;
    }
}
