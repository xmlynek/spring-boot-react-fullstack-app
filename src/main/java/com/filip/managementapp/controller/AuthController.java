package com.filip.managementapp.controller;

import com.filip.managementapp.dto.UserRegistrationRequest;
import com.filip.managementapp.dto.UsernamePasswordAuthRequest;
import com.filip.managementapp.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin("*")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid UsernamePasswordAuthRequest authRequest) {
            return authService.login(authRequest);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid UserRegistrationRequest userRegistrationRequest) {
        return authService.registerUser(userRegistrationRequest);
    }
}
