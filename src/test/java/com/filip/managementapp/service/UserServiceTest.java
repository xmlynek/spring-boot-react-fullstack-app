package com.filip.managementapp.service;

import com.filip.managementapp.dto.UserDto;
import com.filip.managementapp.dto.UserRequest;
import com.filip.managementapp.exception.ResourceAlreadyExistsException;
import com.filip.managementapp.exception.ResourceNotFoundException;
import com.filip.managementapp.model.Gender;
import com.filip.managementapp.model.Role;
import com.filip.managementapp.model.RoleName;
import com.filip.managementapp.model.User;
import com.filip.managementapp.repository.UserRepository;
import org.apache.catalina.realm.GenericPrincipal;
import org.assertj.core.util.Sets;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.Principal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private RoleService roleService;
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
                Sets.set(new Role(1L, RoleName.ROLE_ADMIN, new HashSet<>()))
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

    @Test
    void getCurrentlyLoggedUserShouldThrowAuthenticationCredentialsNotFoundException() {
        assertThatThrownBy(() -> userService.getCurrentlyLoggedUser(null))
                .isInstanceOf(AuthenticationCredentialsNotFoundException.class)
                .hasMessage("Unauthorized");

        verify(userRepository, never()).findByEmail(any());
    }

    @Test
    void shouldFindListOfAllUsers() {
        List<User> users = List.of(
                this.user,
                new User(2L,
                        "Mark",
                        "Kebabos",
                        "kebabos@email.com",
                        "$2a$10$x4yGScMFaIArGQS61h814ODoQ9r1qZSaiYjyVyLejI52JoP.EHm6e",
                        Gender.MALE,
                        LocalDate.of(1999,7,14),
                        false,
                        Set.of(new Role(2L, RoleName.ROLE_USER, new HashSet<>()))
                ),
                new User(2L,
                        "Alicia",
                        "Marcus",
                        "marcusAlicia@email.com",
                        "$2a$10$x4yGScMFaIArGQS61h814ODoQ9r1qZSaiYjyVyLejI52JoP.EHm6e",
                        Gender.FEMALE,
                        LocalDate.of(1989,12,26),
                        false,
                        Set.of(new Role(2L, RoleName.ROLE_USER, new HashSet<>()))
                )
        );
        List<UserDto> expectedUserDtosList = users.stream().map(UserDto::map).toList();

        given(userRepository.findAll()).willReturn(users);

        List<UserDto> userDtos = userService.findAllUsers();

        assertThat(userDtos)
                .hasSize(users.size())
                .isEqualTo(expectedUserDtosList);
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void findAllUsersShouldReturnEmptyList() {
        given(userRepository.findAll()).willReturn(new ArrayList<>());

        List<UserDto> userDtos = userService.findAllUsers();

        assertTrue(userDtos.isEmpty());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void shouldFindUserById() {
        Long userId = this.user.getId();
        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        UserDto userDto = userService.findUserById(userId);

        assertThat(userDto)
                .isNotNull()
                .isEqualTo(UserDto.map(user));
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void findUserByIdShouldThrowResourceNotFoundException() {
        Long userId = 123L;
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findUserById(userId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(String.format("User with id %d not found", userId));

        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void shouldCreateUser() {
        UserRequest userRequest = new UserRequest(
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getGender().name(),
                "Password1",
                user.getBirthDate(),
                true,
                List.of(RoleName.ROLE_ADMIN)
        );
        String email = userRequest.email();
        given(userRepository.existsByEmail(email)).willReturn(false);
        given(userRepository.save(any())).willReturn(user);
        given(passwordEncoder.encode(userRequest.password()))
                .willReturn("$2a$10$x4yGScMFaIArGQS61h814ODoQ9r1qZSaiYjyVyLejI52JoP.EHm6e");
        given(roleService.getIfExistsByNameOrCreateRoles(userRequest.roles().toArray(RoleName[]::new)))
                .willCallRealMethod();

        UserDto createdUserDto = userService.createUser(userRequest);

        assertThat(createdUserDto)
                .isNotNull()
                .isEqualTo(UserDto.map(user));
        verify(userRepository, times(1)).existsByEmail(email);
        verify(userRepository, times(1)).save(any());
        verify(passwordEncoder, times(1)).encode(userRequest.password());
        verify(roleService, times(1))
                .getIfExistsByNameOrCreateRoles(userRequest.roles().toArray(RoleName[]::new));
    }

    @Test
    void createUserShouldThrowResourceAlreadyExistsException() {
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
        String email = userRequest.email();
        given(userRepository.existsByEmail(email)).willReturn(true);

        assertThatThrownBy(() -> userService.createUser(userRequest))
                .isInstanceOf(ResourceAlreadyExistsException.class)
                .hasMessage("User with given email already exists");

        verify(userRepository, times(1)).existsByEmail(email);
        verify(userRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(userRequest.password());
    }

    @Test
    void shouldUpdateUser() {
        UserDto userDtoRequest = UserDto.builder()
                .firstName("NewName")
                .lastName("NewLastname")
                .email("newEmail@email123")
                .gender(user.getGender().name())
                .isEnabled(true)
                .birthDate(user.getBirthDate())
                .roles(List.of(RoleName.ROLE_USER.name()))
                .build();

        User expectedUpdatedUser =
                new User(user.getId(),
                        userDtoRequest.firstName(),
                        userDtoRequest.lastName(),
                        userDtoRequest.email(),
                        user.getPassword(),
                        Gender.valueOf(userDtoRequest.gender()),
                        userDtoRequest.birthDate(),
                        true,
                        Set.of(new Role(1L, RoleName.ROLE_USER, new HashSet<>()))
                );
        Long userId = user.getId();

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(roleService.getIfExistsByNameOrCreateRoles(any()))
                .willCallRealMethod();
        given(userRepository.save(expectedUpdatedUser)).willReturn(expectedUpdatedUser);

        UserDto updatedUser = userService.updateUser(userId, userDtoRequest);

        assertThat(updatedUser)
                .isNotNull()
                .isEqualTo(UserDto.map(expectedUpdatedUser));
        verify(userRepository, times(1)).findById(userId);
        verify(roleService, times(1))
                .getIfExistsByNameOrCreateRoles(any());
        verify(userRepository, times(1)).save(expectedUpdatedUser);
    }

    @Test
    void updateUserShouldThrowResourceNotFoundException() {
        UserDto userDtoRequest = UserDto.builder()
                .firstName("NewName")
                .lastName("NewLastname")
                .email("newEmail@email123")
                .gender(user.getGender().name())
                .isEnabled(true)
                .birthDate(user.getBirthDate())
                .roles(List.of(RoleName.ROLE_USER.name()))
                .build();

        Long userId = user.getId();
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUser(userId, userDtoRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(String.format("User with id %d not found", userId));

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).save(any());
        verify(roleService, never())
                .getIfExistsByNameOrCreateRoles(any());
    }

    @Test
    void shouldDeleteUser() {
        Long userId = user.getId();
        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        userService.deleteUser(userId);

        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).delete(userArgumentCaptor.capture());

        assertThat(userArgumentCaptor.getValue()).isEqualTo(user);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void deleteUserShouldThrowResourceNotFoundException() {
        Long userId = user.getId();
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> userService.deleteUser(userId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(String.format("User with id %d not found", userId));

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).delete(user);
    }
}