package com.filip.managementapp.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.stream.Stream;

public class IsEnumValueValidator implements ConstraintValidator<IsEnumValue, CharSequence> {

    private List<String> acceptedValues;

    public IsEnumValueValidator(List<String> acceptedValues) {
        this.acceptedValues = acceptedValues;
    }

    public IsEnumValueValidator() {
    }

    @Override
    public void initialize(IsEnumValue constraintAnnotation) {
        acceptedValues = Stream.of(constraintAnnotation.enumClass().getEnumConstants())
                .map(Enum::name)
                .toList();
    }

    @Override
    public boolean isValid(CharSequence value, ConstraintValidatorContext constraintValidatorContext) {
        return value != null && acceptedValues.contains(value.toString());
    }
}
