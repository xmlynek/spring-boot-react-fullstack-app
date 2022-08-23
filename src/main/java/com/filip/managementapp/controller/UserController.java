package com.filip.managementapp.controller;

import com.filip.managementapp.dto.UserDto;
import com.filip.managementapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@CrossOrigin("*")
public class UserController {

    private final UserService userService;

    @GetMapping("/current-user")
    public UserDto getCurrentUser(Principal principal) {
        return userService.getCurrentlyLoggedUser(principal);
    }


}