package com.filip.managementapp.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class IsEnumValueValidator implements ConstraintValidator<IsEnumValue, CharSequence> {

    private List<String> acceptedValues;

    @Override
    public void initialize(IsEnumValue constraintAnnotation) {
        acceptedValues = Stream.of(constraintAnnotation.enumClass().getEnumConstants())
                .map(Enum::name)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isValid(CharSequence value, ConstraintValidatorContext constraintValidatorContext) {
        return value != null && acceptedValues.contains(value.toString());
    }
}
