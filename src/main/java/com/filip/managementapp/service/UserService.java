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
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final RoleService roleService;

    @Transactional(readOnly = true)
    public UserDto getCurrentlyLoggedUser(Principal principal) {
        if (principal == null) {
            throw new AuthenticationCredentialsNotFoundException("Unauthorized");
        }
        return UserDto.map(userRepository.findByEmail(principal.getName())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found")));
    }

    @Transactional(readOnly = true)
    public List<UserDto> findAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserDto::map)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserDto findUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("User with id %d not found", id)));
        return UserDto.map(user);
    }

    @Transactional
    public UserDto createUser(UserRequest userRequest) {
        if (userRepository.existsByEmail(userRequest.email())) {
            throw new ResourceAlreadyExistsException("User with given email already exists");
        }

        Set<Role> roles = roleService.getIfExistsByNameOrCreateRoles(userRequest.roles().toArray(RoleName[]::new));

        User createdUser = User.builder()
                .firstName(userRequest.firstName())
                .lastName(userRequest.lastName())
                .email(userRequest.email())
                .password(passwordEncoder.encode(userRequest.password()))
                .gender(Gender.valueOf(userRequest.gender()))
                .birthDate(userRequest.birthDate())
                .isEnabled(userRequest.isEnabled())
                .roles(roles)
                .build();

        return UserDto.map(userRepository.save(createdUser));
    }

    @Transactional
    public UserDto updateUser(Long id, UserDto userDtoRequest) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("User with id %d not found", id)));

        Set<Role> roles = roleService.getIfExistsByNameOrCreateRoles(
                userDtoRequest.roles().stream().map(RoleName::valueOf).toArray(RoleName[]::new)
        );

        user.setFirstName(userDtoRequest.firstName());
        user.setLastName(userDtoRequest.lastName());
        user.setEmail(userDtoRequest.email());
        user.setGender(Gender.valueOf(userDtoRequest.gender()));
        user.setBirthDate(userDtoRequest.birthDate());
        user.setEnabled(userDtoRequest.isEnabled());
        user.getRoles().clear();
        user.setRoles(roles);

        return UserDto.map(userRepository.save(user));
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("User with id %d not found", id)));
        userRepository.delete(user);
    }

}
