package com.filip.managementapp.service;

import com.filip.managementapp.model.Gender;
import com.filip.managementapp.model.Role;
import com.filip.managementapp.model.RoleName;
import com.filip.managementapp.model.User;
import com.filip.managementapp.repository.UserRepository;
import com.filip.managementapp.security.MyUserDetails;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MyUserDetailsServiceTest {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private MyUserDetailsService myUserDetailsService;
    private final User user;

    public MyUserDetailsServiceTest() {
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
    void loadUserByUsernameWillReturnUserDetails() {
        given(userRepository.findByEmail(user.getEmail())).willReturn(Optional.of(user));

        UserDetails userDetails = myUserDetailsService.loadUserByUsername(user.getEmail());
        assertThat(userDetails).isInstanceOf(MyUserDetails.class);
        assertThat(((MyUserDetails) userDetails).getUser()).isEqualTo(this.user);

        verify(userRepository, times(1)).findByEmail(user.getEmail());
    }

    @Test
    void loadUserByUsernameWillThrowException() {
        String userEmail = user.getEmail();
        given(userRepository.findByEmail(user.getEmail())).willReturn(Optional.empty());

        assertThatThrownBy(() -> myUserDetailsService.loadUserByUsername(userEmail))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage(String.format("User with email '%s' not found", user.getEmail()));

        verify(userRepository, times(1)).findByEmail(user.getEmail());
    }
}