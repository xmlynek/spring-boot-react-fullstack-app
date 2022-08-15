package com.filip.managementapp.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.filip.managementapp.model.Gender;
import com.filip.managementapp.model.User;
import lombok.Builder;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Builder
public record UserDto (Long id,
                      String firstName,

                      String lastName,
                      String email,
                      Gender gender,
                      @JsonFormat(
                              shape = JsonFormat.Shape.STRING,
                              pattern = "yyyy-MM-dd",
                              timezone = "Europe/Bratislava")
                       Date birthDate,
                      List<String> roles) {

    public static UserDto map(User user) {
        return UserDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .gender(user.getGender())
                .birthDate(user.getBirthDate())
                .roles(
                        user.getRoles().stream()
                                .map(role -> role.getName().name())
                                .collect(Collectors.toList())
                )
                .build();
    }
}
