package com.filip.managementapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.filip.managementapp.AbstractControllerITest;
import com.filip.managementapp.dto.UserDto;
import com.filip.managementapp.dto.UserRequest;
import com.filip.managementapp.exception.ResourceAlreadyExistsException;
import com.filip.managementapp.exception.ResourceNotFoundException;
import com.filip.managementapp.mapper.UserMapper;
import com.filip.managementapp.model.Gender;
import com.filip.managementapp.model.Role;
import com.filip.managementapp.model.RoleName;
import com.filip.managementapp.model.User;
import com.filip.managementapp.repository.RoleRepository;
import com.filip.managementapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class UserControllerITest extends AbstractControllerITest {

    private final String API_USERS_URL = "/api/v1/users";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    private final User user;

    public UserControllerITest() {
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

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "email123@gmail.com")
    void shouldGetCurrentUser() throws Exception {
        User savedUser = userRepository.save(user);

        MvcResult result = mockMvc.perform(get(API_USERS_URL + "/current-user"))
                .andExpect(status().isOk())
                .andReturn();

        assertThat(objectMapper.readValue(result.getResponse().getContentAsString(), UserDto.class))
                .isEqualTo(userMapper.userToUserDto(savedUser));
    }

    @Test
    void getCurrentUserShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get(API_USERS_URL + "/current-user"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "username123")
    void getCurrentUserShouldReturnNotFound() throws Exception {
        mockMvc.perform(get(API_USERS_URL + "/current-user"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "email123@gmail.com", roles = {"ADMIN"})
    void shouldFindAllUsers() throws Exception {
        Role userRole = roleRepository.save(new Role(2L, RoleName.ROLE_USER, new HashSet<>()));

        List<User> users = List.of(
                new User(100L,
                        "Mark",
                        "Kebabos",
                        "kebabos@email.com",
                        "$2a$10$x4yGScMFaIArGQS61h814ODoQ9r1qZSaiYjyVyLejI52JoP.EHm6e",
                        Gender.MALE,
                        LocalDate.of(1999,7,14),
                        false,
                        Set.of(userRole)
                ),
                new User(300L,
                        "Alicia",
                        "Marcus",
                        "marcusAlicia@email.com",
                        "$2a$10$x4yGScMFaIArGQS61h814ODoQ9r1qZSaiYjyVyLejI52JoP.EHm6e",
                        Gender.FEMALE,
                        LocalDate.of(1989,12,26),
                        false,
                        Set.of(userRole)
                )
        );
        List<User> savedUsers = userRepository.saveAllAndFlush(users);

        var response = mockMvc.perform(get(API_USERS_URL))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        assertThat(objectMapper.readValue(response.getContentAsString(), UserDto[].class))
                .isEqualTo(savedUsers.stream().map(userMapper::userToUserDto).toArray());
    }

    @Test
    @WithMockUser(username = "email123@gmail.com", roles = {"ADMIN"})
    void findAllUsersShouldReturnEmptyList() throws Exception {
        var response = mockMvc.perform(get(API_USERS_URL))
                .andExpect(status().isOk())
                .andReturn().getResponse();
        assertThat(objectMapper.readValue(response.getContentAsString(), UserDto[].class)).isEmpty();
        assertThat(response.getContentAsString()).isEqualTo("[]");
    }

    @Test
    @WithMockUser(username = "email123@gmail.com")
    void findAllUsersShouldReturnForbidden() throws Exception {
        mockMvc.perform(get(API_USERS_URL)).andExpect(status().isForbidden());
    }

    @Test
    void findAllUsersShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get(API_USERS_URL)).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "email123@gmail.com", roles = {"ADMIN"})
    void shouldFindUserById() throws Exception {
        User savedUser = userRepository.save(user);

        MvcResult result = mockMvc.perform(get(String.format("%s/%d", API_USERS_URL , savedUser.getId())))
                .andExpect(status().isOk())
                .andReturn();

        assertThat(objectMapper.readValue(result.getResponse().getContentAsString(), UserDto.class))
                .isEqualTo(userMapper.userToUserDto(savedUser));
    }

    @Test
    @WithMockUser(username = "email123@gmail.com", roles = {"ADMIN"})
    void findUserByIdShouldReturnNotFoundException() throws Exception {
        Long userId = 10L;
        MvcResult result = mockMvc.perform(get(String.format("%s/%d", API_USERS_URL, userId)))
                .andExpect(status().isNotFound())
                .andReturn();

        assertTrue(result.getResolvedException() instanceof ResourceNotFoundException);
        assertThat(result.getResolvedException().getMessage())
                .isEqualTo(String.format("User with id %d not found", userId));
        assertThat(result.getResponse().getContentAsString()).isNotEmpty();
    }

    @Test
    void findUserByIdShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get(String.format("%s/%d", API_USERS_URL, 1))).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "email123@gmail.com")
    void findUserByIdShouldReturnForbidden() throws Exception {
        mockMvc.perform(get(String.format("%s/%d", API_USERS_URL, 1))).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "email123@gmail.com", roles = {"ADMIN"})
    void shouldCreateUser() throws Exception {
        UserRequest userRequest = new UserRequest(
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getGender().name(),
                "Password1",
                user.getBirthDate(),
                true,
                List.of(RoleName.ROLE_USER)
        );

        MvcResult result = mockMvc.perform(
                post(API_USERS_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequest))
        )
                .andExpect(status().isCreated())
                .andReturn();

        UserDto createdUserResponse = objectMapper.readValue(result.getResponse().getContentAsString(), UserDto.class);
        List<User> userList = userRepository.findAll();

        assertThat(createdUserResponse).isNotNull();
        assertThat(userList).hasSize(1);
        assertThat(createdUserResponse).isEqualTo(userMapper.userToUserDto(userList.get(0)));
    }

    @Test
    @WithMockUser(username = "email123@gmail.com", roles = {"ADMIN"})
    void createUserShouldReturnResourceAlreadyExistsException() throws Exception {
        UserRequest userRequest = new UserRequest(
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getGender().name(),
                "Password1",
                user.getBirthDate(),
                true,
                List.of(RoleName.ROLE_USER)
        );

        userRepository.save(user);

        MvcResult result = mockMvc.perform(
                post(API_USERS_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequest))
        )
                .andExpect(status().isBadRequest())
                .andReturn();

        assertTrue(result.getResolvedException() instanceof ResourceAlreadyExistsException);
        assertThat(result.getResolvedException().getMessage()).isEqualTo("User with given email already exists");
    }

    @Test
    @WithMockUser(username = "email123@gmail.com", roles = {"ADMIN"})
    void createUserShouldReturnMethodArgumentNotValidException() throws Exception {
        UserRequest userRequest = new UserRequest(
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getGender().name(),
                "password1",
                user.getBirthDate(),
                true,
                List.of(RoleName.ROLE_USER)
        );

        MvcResult result = mockMvc.perform(
                        post(API_USERS_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(userRequest))
                )
                .andExpect(status().isBadRequest())
                .andReturn();

        assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException);
        assertThat(result.getResponse().getContentAsString()).contains("Password must contain at least 1 uppercase letter, " +
                "1 lowercase letter and 1 number, with greater length than 8 characters");
    }

    @Test
    void createUserShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(post(API_USERS_URL)).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "email123@gmail.com")
    void createUserShouldReturnForbidden() throws Exception {
        UserRequest userRequest = new UserRequest(
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getGender().name(),
                "Password1",
                user.getBirthDate(),
                true,
                List.of(RoleName.ROLE_USER)
        );
        mockMvc.perform(
                        post(API_USERS_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(userRequest))
                )
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "email123@gmail.com", roles = {"ADMIN"})
    void shouldUpdateUser() throws Exception {
        User savedUser = userRepository.saveAndFlush(user);
        UserDto userDtoRequest = UserDto.builder()
                .firstName("NewName")
                .lastName("NewLastname")
                .email("newEmail@email123")
                .gender(user.getGender().name())
                .isEnabled(true)
                .birthDate(user.getBirthDate())
                .roles(List.of(RoleName.ROLE_USER.name()))
                .build();
        Long userId = savedUser.getId();

        MvcResult result = mockMvc.perform(
                put(String.format("%s/%d", API_USERS_URL, userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDtoRequest))
                )
                .andExpect(status().isOk())
                .andReturn();

        Optional<User> foundedUpdatedUser = userRepository.findByEmail(userDtoRequest.email());
        Optional<User> foundedOriginalUser = userRepository.findByEmail(user.getEmail());

        assertThat(foundedOriginalUser).isEmpty();
        assertThat(foundedUpdatedUser).isPresent();
        assertThat(objectMapper.readValue(result.getResponse().getContentAsString(), UserDto.class))
                .isEqualTo(userMapper.userToUserDto(foundedUpdatedUser.get()));
    }

    @Test
    @WithMockUser(username = "email123@gmail.com", roles = {"ADMIN"})
    void updateUserShouldReturnNotFound() throws Exception {
        User savedUser = userRepository.saveAndFlush(user);
        UserDto userDtoRequest = UserDto.builder()
                .firstName("NewName")
                .lastName("NewLastname")
                .email("newEmail@email123")
                .gender(user.getGender().name())
                .isEnabled(true)
                .birthDate(user.getBirthDate())
                .roles(List.of(RoleName.ROLE_USER.name()))
                .build();
        Long userId = 123L;

        MvcResult result = mockMvc.perform(
                        put(String.format("%s/%d", API_USERS_URL, userId))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(userDtoRequest))
                )
                .andExpect(status().isNotFound())
                .andReturn();

        Optional<User> foundedUpdatedUser = userRepository.findByEmail(userDtoRequest.email());
        Optional<User> foundedOriginalUser = userRepository.findByEmail(user.getEmail());

        assertThat(foundedOriginalUser).isPresent();
        assertThat(savedUser).isEqualTo(foundedOriginalUser.get());
        assertThat(foundedUpdatedUser).isEmpty();
        assertTrue(result.getResolvedException() instanceof ResourceNotFoundException);
        assertThat(result.getResolvedException().getMessage())
                .isEqualTo(String.format("User with id %d not found", userId));
    }

    @Test
    @WithMockUser(username = "email123@gmail.com", roles = {"ADMIN"})
    void updateUserShouldReturnMethodArgumentNotValidException() throws Exception {
        User savedUser = userRepository.saveAndFlush(user);
        UserDto userDtoRequest = UserDto.builder()
                .firstName("NewName")
                .lastName("")
                .email("newEmail@email123")
                .gender(user.getGender().name())
                .isEnabled(true)
                .birthDate(user.getBirthDate())
                .roles(List.of(RoleName.ROLE_USER.name()))
                .build();
        Long userId = savedUser.getId();

        MvcResult result = mockMvc.perform(
                        put(String.format("%s/%d", API_USERS_URL, userId))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(userDtoRequest))
                )
                .andExpect(status().isBadRequest())
                .andReturn();

        Optional<User> foundedUpdatedUser = userRepository.findByEmail(userDtoRequest.email());
        Optional<User> foundedOriginalUser = userRepository.findByEmail(user.getEmail());

        assertThat(foundedOriginalUser).isPresent();
        assertThat(savedUser).isEqualTo(foundedOriginalUser.get());
        assertThat(foundedUpdatedUser).isEmpty();
        assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException);
        assertThat(result.getResponse().getContentAsString()).contains("Lastname is required");
    }

    @Test
    void updateUserShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(put(String.format("%s/%d", API_USERS_URL, 123))).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "email123@gmail.com")
    void updateUserShouldReturnForbidden() throws Exception {
        UserDto userDtoRequest = UserDto.builder()
                .firstName("NewName")
                .lastName("NewLastname")
                .email("newEmail@email123")
                .gender(user.getGender().name())
                .isEnabled(true)
                .birthDate(user.getBirthDate())
                .roles(List.of(RoleName.ROLE_USER.name()))
                .build();
        Long userId = 123L;

        mockMvc.perform(
                put(String.format("%s/%d", API_USERS_URL, userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDtoRequest))
        ).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "email123@gmail.com", roles = {"ADMIN"})
    void shouldDeleteUser() throws Exception {
        User savedUser = userRepository.saveAndFlush(user);
        Long userId = savedUser.getId();

        mockMvc.perform(delete(String.format("%s/%d", API_USERS_URL, userId)))
                .andExpect(status().isNoContent());

        List<User> users = userRepository.findAll();
        Optional<User> deletedUser = userRepository.findById(userId);

        assertThat(users).isEmpty();
        assertThat(deletedUser).isEmpty();
    }

    @Test
    @WithMockUser(username = "email123@gmail.com", roles = {"ADMIN"})
    void deleteUserShouldReturnNotFound() throws Exception {
        User savedUser = userRepository.saveAndFlush(user);
        Long userId = 12345L;

        MvcResult result = mockMvc.perform(delete(String.format("%s/%d", API_USERS_URL, userId)))
                .andExpect(status().isNotFound())
                .andReturn();

        List<User> users = userRepository.findAll();
        Optional<User> foundedUser = userRepository.findById(savedUser.getId());

        assertThat(users).hasSize(1);
        assertThat(foundedUser).isPresent();
        assertTrue(result.getResolvedException() instanceof ResourceNotFoundException);
        assertThat(result.getResolvedException().getMessage())
                .isEqualTo(String.format("User with id %d not found", userId));
    }

    @Test
    @WithMockUser(username = "email123@gmail.com")
    void deleteUserShouldReturnForbidden() throws Exception {
        User savedUser = userRepository.saveAndFlush(user);
        Long userId = savedUser.getId();

        mockMvc.perform(delete(String.format("%s/%d", API_USERS_URL, userId)))
                .andExpect(status().isForbidden());

        List<User> users = userRepository.findAll();
        Optional<User> foundedUser = userRepository.findById(savedUser.getId());

        assertThat(users).hasSize(1);
        assertThat(foundedUser).isPresent();
    }

    @Test
    void deleteUserShouldReturnUnauthorized() throws Exception {
        User savedUser = userRepository.saveAndFlush(user);
        Long userId = savedUser.getId();

        mockMvc.perform(delete(String.format("%s/%d", API_USERS_URL, userId)))
                .andExpect(status().isUnauthorized());

        List<User> users = userRepository.findAll();
        Optional<User> foundedUser = userRepository.findById(savedUser.getId());

        assertThat(users).hasSize(1);
        assertThat(foundedUser).isPresent();
    }
}