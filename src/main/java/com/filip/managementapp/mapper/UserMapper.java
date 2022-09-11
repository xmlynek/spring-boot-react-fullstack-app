package com.filip.managementapp.mapper;

import com.filip.managementapp.dto.UserDto;
import com.filip.managementapp.dto.UserRegistrationRequest;
import com.filip.managementapp.dto.UserRequest;
import com.filip.managementapp.model.Role;
import com.filip.managementapp.model.RoleName;
import com.filip.managementapp.model.User;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface UserMapper {

    UserDto userToUserDto(User user);

    User userRegistrationRequestToUser(UserRegistrationRequest userRegistrationRequest);

    User userRequestToUser(UserRequest userRequest);

    User userDtoToUser(UserDto userDto);

    default List<String> rolesToListOfStrings(Set<Role> roles) {
        return roles.stream().map(role -> role.getName().name()).toList();
    }

    default Role stringToRole(String role) {
        return role != null ? new Role( RoleName.valueOf(role) ) : null;
    }

    default String roleToString(Role role) {
        return role != null ? role.getName().name() : null;
    }
}
