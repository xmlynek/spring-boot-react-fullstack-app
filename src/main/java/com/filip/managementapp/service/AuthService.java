package com.filip.managementapp.service;

import com.filip.managementapp.dto.UserDto;
import com.filip.managementapp.dto.UserRegistrationRequest;
import com.filip.managementapp.dto.UsernamePasswordAuthRequest;
import com.filip.managementapp.model.Gender;
import com.filip.managementapp.model.Role;
import com.filip.managementapp.model.RoleName;
import com.filip.managementapp.model.User;
import com.filip.managementapp.repository.UserRepository;
import com.filip.managementapp.security.MyUserDetails;
import com.filip.managementapp.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityExistsException;
import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final SecurityUtils securityUtils;

    public ResponseEntity<?> login(UsernamePasswordAuthRequest authRequest) {
        Authentication authentication = authenticationManager
                .authenticate(
                        new UsernamePasswordAuthenticationToken(authRequest.username(), authRequest.password())
                );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();

        ResponseCookie responseJwtCookie = securityUtils.generateJwtCookie(authentication);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, responseJwtCookie.toString())
                .body(UserDto.map(userDetails.getUser()));
    }

    @Transactional
    public ResponseEntity<?> registerUser(UserRegistrationRequest userRegistrationRequest) {
        if (userRepository.existsByEmail(userRegistrationRequest.email())) {
            throw new EntityExistsException("User with given username already exists");
        }
        if(!userRegistrationRequest.password().equals(userRegistrationRequest.confirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        User user = User.builder()
                        .firstName(userRegistrationRequest.firstName())
                        .lastName(userRegistrationRequest.lastName())
                        .email(userRegistrationRequest.email())
                        .password(passwordEncoder.encode(userRegistrationRequest.password()))
                        .gender(Gender.valueOf(userRegistrationRequest.gender()))
                        .birthDate(userRegistrationRequest.birthDate())
                        .roles(new HashSet<>())
                        .isEnabled(true)
                        .build();

        Role role = roleService.existsByName(RoleName.USER)
                ? roleService.findRoleByName(RoleName.USER)
                : roleService.createRole(RoleName.USER);

        user.getRoles().add(role);
        userRepository.save(user);
        return ResponseEntity.ok().body("Registration was successful. Now log in.");
    }

    public ResponseEntity<?> logout() {
        ResponseCookie jwtCookie = securityUtils.deleteJwtCookie();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .build();
    }
}
