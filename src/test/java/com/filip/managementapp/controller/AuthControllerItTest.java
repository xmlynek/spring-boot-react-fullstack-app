package com.filip.managementapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.filip.managementapp.AbstractControllerITest;
import com.filip.managementapp.dto.UserDto;
import com.filip.managementapp.dto.UserRegistrationRequest;
import com.filip.managementapp.dto.UsernamePasswordAuthRequest;
import com.filip.managementapp.exception.ResourceAlreadyExistsException;
import com.filip.managementapp.model.Gender;
import com.filip.managementapp.model.User;
import com.filip.managementapp.repository.UserRepository;
import com.filip.managementapp.util.SecurityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.LocalDate;
import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


public class AuthControllerItTest extends AbstractControllerITest {

    private final String API_AUTH_URL = "/api/v1/auth";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;


    @Autowired
    private SecurityUtils securityUtils;

    private final UserRegistrationRequest registrationRequest;
    private final User userModel;

    public AuthControllerItTest() {
        registrationRequest = new UserRegistrationRequest(
                "Filip",
                "Mlýnek",
                "email123@gmail.com",
                "MALE",
                "Password1",
                "Password1",
                LocalDate.of(2000, 3, 3)
        );
        userModel = new User(1L,
                "Username",
                "Lastname",
                this.registrationRequest.email(),
                "$2a$10$x4yGScMFaIArGQS61h814ODoQ9r1qZSaiYjyVyLejI52JoP.EHm6e", // Password1
                Gender.OTHER,
                this.registrationRequest.birthDate(),
                true,
                new HashSet<>()
        );
    }

    @BeforeEach
    public void setup() {
        userRepository.deleteAll();
        userRepository.flush();
    }

    @Test
    void shouldRegisterUser() throws Exception {
        ResultActions resultActions = mockMvc.perform(post(API_AUTH_URL + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(this.registrationRequest))
        );

        resultActions.andExpect(status().isOk());
        var user = userRepository.findByEmail(this.registrationRequest.email());
        assertThat(user).isNotEmpty();
    }

    @Test
    void shouldNotRegisterUserWithExistingEmail() throws Exception {
        userRepository.saveAndFlush(this.userModel);

        ResultActions resultActions = mockMvc.perform(post(API_AUTH_URL + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(this.registrationRequest))
        );

        var users = userRepository.findAll();
        var user = userRepository.findByEmail(this.registrationRequest.email());

        resultActions.andExpect(status().isBadRequest());
        assertThat(users.size()).isEqualTo(1);
        assertThat(user).isNotEmpty();
        assertThat(user.get().getFirstName()).isEqualTo(this.userModel.getFirstName());
    }

    @Test
    void registrationShouldThrowResourceAlreadyExistsException_WhenUserWithEmailExists() throws Exception {
        userRepository.saveAndFlush(this.userModel);

        ResultActions resultActions = mockMvc.perform(post(API_AUTH_URL + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(this.registrationRequest))
        );

        resultActions.andExpect(status().isBadRequest());
        assertTrue(resultActions.andReturn().getResolvedException() instanceof ResourceAlreadyExistsException);
        assertThat(resultActions.andReturn().getResolvedException().getMessage()).isEqualTo("User with given email already exists");
        assertThat(resultActions.andReturn().getResponse().getContentAsString()).contains("User with given email already exists");
    }

    @Test
    void registerUserWillThrowExceptionWithInvalidGenderTypeMessage() throws Exception {
        UserRegistrationRequest userRegistrationRequest =
                new UserRegistrationRequest(
                        "Filip",
                        "Mlynek",
                        "email123@gmail.com",
                        "MALEs",
                        "Password1",
                        "Password1",
                        LocalDate.of(2000, 3, 3)
                );
        var result = mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRegistrationRequest))
        ).andReturn();


        var users = userRepository.findAll();
        assertThat(users.size()).isEqualTo(0);
        assertThat(result.getResponse().getStatus()).isEqualTo(400);
        assertThat(result.getResponse().getContentAsString()).contains("Invalid gender type");
        assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException);
    }

    @Test
    void shouldLoginUserAndReturnUserDtoAsResponse() throws Exception {
        UsernamePasswordAuthRequest authRequest =
                new UsernamePasswordAuthRequest(this.userModel.getEmail(), "Password1");

        User savedUser = userRepository.saveAndFlush(this.userModel);

        mockMvc.perform(post(API_AUTH_URL + "/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authRequest))
        ).andExpect(status().isOk())
                .andExpect(cookie().exists(securityUtils.getJwtCookieName()))
                .andExpect(cookie().httpOnly(securityUtils.getJwtCookieName(), true))
                .andExpect(cookie().path(securityUtils.getJwtCookieName(), "/"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(UserDto.map(savedUser))));
    }

    @Test
    void loginShouldThrowBadCredentialsException() throws Exception {
        UsernamePasswordAuthRequest authRequest =
                new UsernamePasswordAuthRequest(this.userModel.getEmail(), "Password2");

        userRepository.saveAndFlush(this.userModel);

        ResultActions resultActions = mockMvc.perform(post(API_AUTH_URL + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest))
                );

        resultActions.andExpect(status().isUnauthorized());
        assertTrue(resultActions.andReturn().getResolvedException() instanceof BadCredentialsException);
        assertThat(resultActions.andReturn().getResponse().getContentAsString()).contains("Bad credentials");
    }

    @Test
    void loginValidationShouldThrowMethodArgumentNotValidException() throws Exception {
        UsernamePasswordAuthRequest authRequest =
                new UsernamePasswordAuthRequest(null, "Password1");

        userRepository.saveAndFlush(this.userModel);

        ResultActions resultActions = mockMvc.perform(post(API_AUTH_URL + "/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authRequest))
        );

        resultActions.andExpect(status().isBadRequest());
        assertTrue(resultActions.andReturn().getResolvedException() instanceof MethodArgumentNotValidException);
        assertThat(resultActions.andReturn().getResponse().getContentAsString()).contains("Username is required");
    }

    @Test
    void shouldLogoutAndContainRemoveCookie() throws Exception {
        mockMvc.perform(
            post(API_AUTH_URL + "/logout")
            )
            .andExpect(status().isOk())
            .andExpect(cookie().exists(securityUtils.getJwtCookieName()))
            .andExpect(cookie().exists(securityUtils.getJwtCookieName()))
            .andExpect(cookie().value(securityUtils.getJwtCookieName(), ""))
            .andExpect(cookie().path(securityUtils.getJwtCookieName(), "/"))
            .andExpect(cookie().maxAge(securityUtils.getJwtCookieName(), 0))
            .andExpect(cookie().httpOnly(securityUtils.getJwtCookieName(), true));
    }
}
