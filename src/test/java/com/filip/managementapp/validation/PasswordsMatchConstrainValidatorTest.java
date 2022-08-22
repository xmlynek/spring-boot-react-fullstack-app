package com.filip.managementapp.validation;

import com.filip.managementapp.AbstractValidationTest;
import com.filip.managementapp.dto.UserRegistrationRequest;
import com.filip.managementapp.model.Gender;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordsMatchConstrainValidatorTest extends AbstractValidationTest {

    private final PasswordsMatchConstrainValidator passwordsMatchConstrainValidator;
    private final UserRegistrationRequest registrationRequest;

    public PasswordsMatchConstrainValidatorTest() {
        passwordsMatchConstrainValidator = new PasswordsMatchConstrainValidator();
        registrationRequest = new UserRegistrationRequest(
                "Test",
                "User",
                "email12@email.com",
                Gender.OTHER.name(),
                "Password1",
                "Password1",
                LocalDate.of(2000, 3, 3)
        );
    }

    @Test
    void isValidShouldReturnTrue() {
        assertTrue(passwordsMatchConstrainValidator.isValid(registrationRequest, null));
    }

    @Test
    void isValidShouldReturnFalse() {
        var registrationRequest = new UserRegistrationRequest(
                "Test",
                "User",
                "email12@email.com",
                Gender.OTHER.name(),
                "Password2",
                "Password1",
                LocalDate.of(2000, 3, 3)
        );
        assertFalse(passwordsMatchConstrainValidator.isValid(registrationRequest, null));
    }

    @Test
    @Override
    public void validatorShouldReturnViolation() {
        UserRegistrationRequest registrationRequest =
                new UserRegistrationRequest(
                        "Test",
                        "User",
                        "email12@email.com",
                        Gender.OTHER.name(),
                        "Password1",
                        "Password13x",
                        LocalDate.of(2000, 3, 3)
                );

        var violations = validator.validate(registrationRequest);
        var violation = violations.stream().toList().get(0);

        assertThat(violations).isNotEmpty();
        assertThat(violation).isNotNull();
        assertThat(violation.getMessage()).isEqualTo("Passwords do not match");
    }

    @Test
    @Override
    public void validatorShouldNotReturnViolation() {
        var violations = validator.validate(registrationRequest);

        assertThat(violations).isEmpty();
    }
}