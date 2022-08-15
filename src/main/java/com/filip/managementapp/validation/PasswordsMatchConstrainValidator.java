package com.filip.managementapp.validation;

import com.filip.managementapp.dto.UserRegistrationRequest;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Code for this validation taken from
 * https://stackoverflow.com/questions/7239897/spring-3-annotation-based-validation-password-and-confirm-password
 */
public class PasswordsMatchConstrainValidator implements
        ConstraintValidator<PasswordsMatchConstraint, Object> {
    @Override
    public boolean isValid(Object candidate, ConstraintValidatorContext constraintValidatorContext) {
        UserRegistrationRequest userRegistrationRequest = (UserRegistrationRequest)  candidate;
        return userRegistrationRequest.password().equals(userRegistrationRequest.confirmPassword());
    }
}
