package com.filip.managementapp.mapper;

import com.filip.managementapp.dto.UserDto;
import com.filip.managementapp.dto.UserRegistrationRequest;
import com.filip.managementapp.dto.UserRequest;
import com.filip.managementapp.model.Gender;
import com.filip.managementapp.model.Role;
import com.filip.managementapp.model.RoleName;
import com.filip.managementapp.model.User;
import org.assertj.core.util.Sets;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest {

    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    private final User user = new User(1L,
            "Username",
            "Lastname",
            "emailos@gmail.com",
            "$2a$10$x4yGScMFaIArGQS61h814ODoQ9r1qZSaiYjyVyLejI52JoP.EHm6e", // Password1
            Gender.OTHER,
            LocalDate.of(2000, 4, 22),
            true,
            new HashSet<>()
    );

    @Test
    void userToUserDtoShouldReturnMappedUser() {
        UserDto mappedUser = userMapper.userToUserDto(user);

        assertThat(mappedUser)
                .isNotNull()
                .hasNoNullFieldsOrProperties()
                .hasFieldOrPropertyWithValue("firstName", "Username");
    }

    @Test
    void userToUserDtoShouldReturnNullWhenNullArgIsPassed() {
        UserDto mappedUser = userMapper.userToUserDto(null);

        assertThat(mappedUser).isNull();
    }

    @Test
    void userDtoToUserShouldReturnUser() {
        UserDto userRequest = UserDto.builder()
                .firstName("NewName")
                .lastName("NewLastname")
                .email("newEmail@email123")
                .gender(user.getGender().name())
                .isEnabled(true)
                .birthDate(user.getBirthDate())
                .roles(List.of(RoleName.ROLE_USER.name()))
                .build();

        User mappedUser = userMapper.userDtoToUser(userRequest);

        assertThat(mappedUser).isNotNull();

    }

    @Test
    void userDtoToUserShouldReturnNull() {
        User user = userMapper.userDtoToUser(null);

        assertThat(user).isNull();
    }

    @Test
    void userRequestToUserShouldReturnUser() {
        UserRequest userRequest = new UserRequest(
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getGender().name(),
                "password1",
                user.getBirthDate(),
                true,
                List.of(RoleName.ROLE_USER, RoleName.ROLE_ADMIN)
        );

        User expectedOutput = User.builder()
                .firstName(userRequest.firstName())
                .lastName(userRequest.lastName())
                .email(userRequest.email())
                .gender(Gender.valueOf(userRequest.gender()))
                .password(userRequest.password())
                .birthDate(userRequest.birthDate())
                .isEnabled(userRequest.isEnabled())
                .roles(userRequest.roles().stream().map(Role::new).collect(Collectors.toSet()))
                .build();

        User mappedUser = userMapper.userRequestToUser(userRequest);

        assertThat(mappedUser)
                .isNotNull()
                .isEqualTo(expectedOutput);
    }

    @Test
    void userRequestToUserShouldReturnNullWhenNullArgIsPassed() {
        User mappedUser = userMapper.userRequestToUser(null);

        assertThat(mappedUser).isNull();
    }

    @Test
    void userRegistrationRequestToUserShouldReturnUser() {
        UserRegistrationRequest registrationRequest = new UserRegistrationRequest(
                "Filip",
                "Mlýnek",
                "email123@gmail.com",
                "MALE",
                "Password1",
                "Password1",
                LocalDate.of(2000, 3, 3)
        );
        User expectedOutput = User.builder()
                .firstName(registrationRequest.firstName())
                .lastName(registrationRequest.lastName())
                .email(registrationRequest.email())
                .gender(Gender.valueOf(registrationRequest.gender()))
                .password(registrationRequest.password())
                .birthDate(registrationRequest.birthDate())
                .build();

        User user = userMapper.userRegistrationRequestToUser(registrationRequest);

        assertThat(user)
                .isNotNull()
                .isEqualTo(expectedOutput);
    }

    @Test
    void userRegistrationRequestToUserShouldReturnNullWhenNullArgIsPassed() {
        User user = userMapper.userRegistrationRequestToUser(null);

        assertThat(user).isNull();
    }

    @Test
    void rolesToListOfStringsShouldPass() {
        Set<Role> roles = Sets.set(
                new Role(1L, RoleName.ROLE_USER, new HashSet<>()),
                new Role(1L, RoleName.ROLE_ADMIN, new HashSet<>())
                );

        List<String> listOfRoleNames = userMapper.rolesToListOfStrings(roles);

        assertThat(listOfRoleNames)
                .isNotEmpty()
                .hasSize(2)
                .containsOnly(RoleName.ROLE_USER.name(), RoleName.ROLE_ADMIN.name());
    }

    @Test
    void stringToRoleShouldReturnNullWhenNullArgIsPassed() {
        Role role = userMapper.stringToRole(null);

        assertThat(role).isNull();
    }

    @Test
    void stringToRoleShouldReturnRole() {
        Role role = userMapper.stringToRole(RoleName.ROLE_USER.name());

        assertThat(role)
                .isNotNull()
                .isEqualTo(new Role(RoleName.ROLE_USER));
    }

    @Test
    void roleToStringShouldPass() {
        Role role = new Role(RoleName.ROLE_ADMIN);
        String output = userMapper.roleToString(role);

        assertThat(output)
                .isNotNull()
                .isEqualTo(role.getName().name());
    }

    @Test
    void roleToStringShouldReturnNullWhenNullArgIsPassed() {
        String output = userMapper.roleToString(null);

        assertThat(output).isNull();
    }
}