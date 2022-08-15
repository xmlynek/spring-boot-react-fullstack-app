package com.filip.managementapp.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.filip.managementapp.model.Gender;

import java.util.Date;

public record UserRegistrationRequest(String firstName,
                                      String lastName,
                                      String email,
                                      Gender gender,
                                      String password,
                                      String confirmPassword,
                                      @JsonFormat(
                                              shape = JsonFormat.Shape.STRING,
                                              pattern = "yyyy-MM-dd",
                                              timezone = "Europe/Bratislava")
                                      Date birthDate) {
}
