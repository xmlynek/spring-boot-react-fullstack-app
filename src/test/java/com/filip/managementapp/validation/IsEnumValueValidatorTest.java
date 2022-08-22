package com.filip.managementapp.validation;

import com.filip.managementapp.AbstractValidationTest;
import com.filip.managementapp.dto.UserRegistrationRequest;
import com.filip.managementapp.model.Gender;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IsEnumValueValidatorTest extends AbstractValidationTest {

    private final IsEnumValueValidator isEnumValueValidator;

    public IsEnumValueValidatorTest() {
        this.isEnumValueValidator = new IsEnumValueValidator(
                Stream.of(Gender.values())
                        .map(Enum::name)
                        .collect(Collectors.toList())
        );
    }

    @Test
    void isValidShouldReturnTrue() {
        assertTrue(Arrays.stream(Gender.values())
                .allMatch(gender -> isEnumValueValidator.isValid(gender.name(), null)));
    }

    @Test
    void isValidShouldReturnFalse() {
        assertFalse(isEnumValueValidator.isValid("RANDOM_GENDER_NAME", null));
    }

    @Test
    @Override
    public void validatorShouldReturnViolation() {
        UserRegistrationRequest registrationRequest =
                new UserRegistrationRequest(
                        "Test",
                        "User",
                        "email12@email.com",
                        "UNKNOWN_GENDER",
                        "Password1",
                        "Password1",
                        LocalDate.of(2000, 3, 3)
                );

        var violations = validator.validateProperty(registrationRequest, "gender");
        var violation = violations.stream().toList().get(0);

        assertThat(violations).isNotEmpty();
        assertThat(violation).isNotNull();
        assertThat(violation.getMessage()).isEqualTo("Invalid gender type");
        assertThat(violation.getInvalidValue()).isEqualTo(registrationRequest.gender());
    }

    @Test
    @Override
    public void validatorShouldNotReturnViolation() {
        UserRegistrationRequest registrationRequest =
                new UserRegistrationRequest(
                        "Test",
                        "User",
                        "email12@email.com",
                        Gender.MALE.name(),
                        "Password1",
                        "Password1",
                        LocalDate.of(2000, 3, 3)
                );

        var violations = validator.validateProperty(registrationRequest, "gender");

        assertThat(violations).isEmpty();
    }
}