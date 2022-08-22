package com.filip.managementapp.service;

import com.filip.managementapp.dto.UserDto;
import com.filip.managementapp.dto.UserRegistrationRequest;
import com.filip.managementapp.dto.UsernamePasswordAuthRequest;
import com.filip.managementapp.exception.ResourceAlreadyExistsException;
import com.filip.managementapp.model.Gender;
import com.filip.managementapp.model.Role;
import com.filip.managementapp.model.RoleName;
import com.filip.managementapp.model.User;
import com.filip.managementapp.repository.UserRepository;
import com.filip.managementapp.security.MyUserDetails;
import com.filip.managementapp.util.SecurityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleService roleService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private SecurityUtils securityUtils;

    @InjectMocks
    private AuthService authService;

    private final UserRegistrationRequest registrationRequest;
    private final User user;
    private final UsernamePasswordAuthRequest authRequest;
    private final UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken;


    public AuthServiceTest() {
        this.registrationRequest = new UserRegistrationRequest(
                "Filip",
                "Mlýnek",
                "email123@gmail.com",
                "MALE",
                "Password1",
                "Password1",
                LocalDate.of(2000, 3, 3)
        );

        this.user = new User(1L,
                "Username",
                "Lastname",
                this.registrationRequest.email(),
                "$2a$10$x4yGScMFaIArGQS61h814ODoQ9r1qZSaiYjyVyLejI52JoP.EHm6e", // Password1
                Gender.OTHER,
                this.registrationRequest.birthDate(),
                true,
                Set.of(new Role(1L, RoleName.ROLE_ADMIN, new HashSet<>()))
        );
        this.authRequest = new UsernamePasswordAuthRequest(this.user.getEmail(), "Password1");
        this.usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(authRequest.username(), authRequest.password());
    }

    @BeforeEach
    void setUp() {
        authService = new AuthService(userRepository, roleService, passwordEncoder, authenticationManager, securityUtils);
    }

    @Test
    void willLoginSuccessfully() {
        ResponseCookie responseCookie = ResponseCookie.from("jwt_token", "JWT_TOKEN_VALUE")
                .path("/")
                .maxAge(1000)
                .httpOnly(true)
                .build();

        Authentication authentication = mock(UsernamePasswordAuthenticationToken.class);

        given(authenticationManager.authenticate(usernamePasswordAuthenticationToken))
                .willReturn(authentication);
        given(authentication.getPrincipal()).willReturn(new MyUserDetails(this.user));
        given(securityUtils.generateJwtCookie(authentication))
                .willReturn(responseCookie);

        var response = authService.login(authRequest);
        var headers = response.getHeaders();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(UserDto.map(this.user));
        assertThat(headers.getFirst(HttpHeaders.SET_COOKIE)).isEqualTo(responseCookie.toString());

        verify(authenticationManager, times(1)).authenticate(usernamePasswordAuthenticationToken);
        verify(authentication, times(1)).getPrincipal();
        verify(securityUtils, times(1)).generateJwtCookie(authentication);
    }

    @Test
    void loginWillThrowBadCredentialsException() {
        given(authenticationManager.authenticate(usernamePasswordAuthenticationToken))
                .willThrow(BadCredentialsException.class);
        assertThatThrownBy(() -> authService.login(authRequest))
                .isInstanceOf(BadCredentialsException.class);
        verify(authenticationManager, times(1)).authenticate(usernamePasswordAuthenticationToken);
    }

    @Test
    void willRegisterUserWithExistingUserRole() {
        Role userRole = new Role(2L, RoleName.ROLE_USER, new HashSet<>());

        given(userRepository.existsByEmail(user.getEmail())).willReturn(false);
        given(roleService.existsByName(RoleName.ROLE_USER)).willReturn(true);
        given(roleService.findRoleByName(RoleName.ROLE_USER)).willReturn(userRole);

        var response = authService.registerUser(registrationRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Registration was successful. Now log in.");

        verify(userRepository, times(1)).existsByEmail(user.getEmail());
        verify(roleService, times(1)).existsByName(RoleName.ROLE_USER);
        verify(roleService, times(1)).findRoleByName(RoleName.ROLE_USER);
    }

    @Test
    void willRegisterUserWithNewlyCreateUserRole() {
        Role userRole = new Role(2L, RoleName.ROLE_USER, new HashSet<>());

        given(userRepository.existsByEmail(user.getEmail())).willReturn(false);
        given(roleService.existsByName(RoleName.ROLE_USER)).willReturn(false);
        given(roleService.createRole(RoleName.ROLE_USER)).willReturn(userRole);

        var response = authService.registerUser(registrationRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Registration was successful. Now log in.");

        verify(userRepository, times(1)).existsByEmail(user.getEmail());
        verify(roleService, times(1)).existsByName(RoleName.ROLE_USER);
        verify(roleService, times(1)).createRole(RoleName.ROLE_USER);
    }

    @Test
    void registerWillThrowResourceAlreadyExistsException() {
        given(userRepository.existsByEmail(user.getEmail())).willReturn(true);

        assertThatThrownBy(() -> authService.registerUser(registrationRequest))
                .isInstanceOf(ResourceAlreadyExistsException.class)
                .hasMessage("User with given email already exists");
        verify(userRepository, times(1)).existsByEmail(user.getEmail());
    }

    @Test
    void willLogoutSuccessfully() {
        ResponseCookie responseCookie = ResponseCookie.from("jwt_token", "")
                .path("/")
                .maxAge(0)
                .httpOnly(true)
                .build();
        given(securityUtils.deleteJwtCookie()).willReturn(responseCookie);

        var response = authService.logout();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(null);
        assertThat(response.getHeaders().getFirst(HttpHeaders.SET_COOKIE)).isEqualTo(responseCookie.toString());

        verify(securityUtils, times(1)).deleteJwtCookie();
    }
}