package com.filip.managementapp.dto;

import javax.validation.constraints.NotBlank;

public record UsernamePasswordAuthRequest(@NotBlank(message = "Username is required") String username,
                                          @NotBlank(message = "Password is required") String password) {

}
