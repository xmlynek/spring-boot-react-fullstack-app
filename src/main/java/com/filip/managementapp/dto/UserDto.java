package com.filip.managementapp.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.filip.managementapp.model.Gender;
import com.filip.managementapp.validation.IsEnumValue;
import lombok.Builder;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Builder
public record UserDto (
        Long id,

        @NotBlank(message = "Firstname is required")
        String firstName,

        @NotBlank(message = "Lastname is required")
        String lastName,

        @NotBlank(message = "Email is required")
        @Email(message = "Wrong format of email")
        String email,

        @IsEnumValue(enumClass = Gender.class, message = "Invalid gender type")
        String gender,

        @JsonFormat(
                shape = JsonFormat.Shape.STRING,
                pattern = "yyyy-MM-dd",
                timezone = "Europe/Bratislava")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        @NotNull(message = "Birth date is required")
        LocalDate birthDate,

        @NotNull(message = "isEnabled is required")
        Boolean isEnabled,

        @NotEmpty(message = "Roles are required")
        @Valid
        List<String> roles
) {
}
