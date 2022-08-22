package com.filip.managementapp.service;

import com.filip.managementapp.dto.UserDto;
import com.filip.managementapp.exception.ResourceNotFoundException;
import com.filip.managementapp.model.Gender;
import com.filip.managementapp.model.Role;
import com.filip.managementapp.model.RoleName;
import com.filip.managementapp.model.User;
import com.filip.managementapp.repository.UserRepository;
import org.apache.catalina.realm.GenericPrincipal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.Principal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserService userService;
    private final User user;

    public UserServiceTest() {
        this.user = new User(1L,
                "Username",
                "Lastname",
                "email123@gmail.com",
                "$2a$10$x4yGScMFaIArGQS61h814ODoQ9r1qZSaiYjyVyLejI52JoP.EHm6e", // Password1
                Gender.OTHER,
                LocalDate.of(2000, 3, 3),
                true,
                Set.of(new Role(1L, RoleName.ROLE_ADMIN, new HashSet<>()))
        );
    }

    @Test
    void shouldGetCurrentlyLoggedUser() {
        Principal principal = new GenericPrincipal(
                user.getEmail(),
                user.getPassword(),
                user.getRoles().stream().map(role -> role.getName().name()).collect(Collectors.toList())
        );
        String email = principal.getName();
        given(userRepository.findByEmail(email)).willReturn(Optional.of(user));

        var response = userService.getCurrentlyLoggedUser(principal);

        assertThat(response).isEqualTo(UserDto.map(user));
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void getCurrentlyLoggedUserShouldThrowResourceNotFoundException() {
        Principal principal = new GenericPrincipal(user.getEmail(), user.getPassword(), null);
        String email = principal.getName();

        given(userRepository.findByEmail(email)).willReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getCurrentlyLoggedUser(principal))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User not found");

        verify(userRepository, times(1)).findByEmail(email);
    }
}