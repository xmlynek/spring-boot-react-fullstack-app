package com.filip.managementapp.dto;

import com.filip.managementapp.model.Gender;
import com.filip.managementapp.validation.IsEnumValue;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;

public record UserRequest(
        @NotBlank(message = "Firstname is required")
        String firstName,
        @NotBlank(message = "Lastname is required")
        String lastName,
        @NotBlank(message = "Email is required")
        @Email(message = "Wrong format of email")
        String email,
        @IsEnumValue(enumClass = Gender.class, message = "Invalid gender type")
        String gender,
        @NotBlank(message = "Password is required")
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$",
                message = "Password must contain at least 1 uppercase letter, " +
                        "1 lowercase letter and 1 number, with greater length than 8 characters")
        String password,
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        @NotNull(message = "Birth date is required")
        LocalDate birthDate,
        @NotNull
        boolean isEnabled
) {

}
