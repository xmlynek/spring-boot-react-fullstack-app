package com.filip.managementapp.service;

import com.filip.managementapp.dto.UserDto;
import com.filip.managementapp.dto.UserRequest;
import com.filip.managementapp.exception.ResourceAlreadyExistsException;
import com.filip.managementapp.exception.ResourceNotFoundException;
import com.filip.managementapp.model.Gender;
import com.filip.managementapp.model.User;
import com.filip.managementapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public UserDto getCurrentlyLoggedUser(Principal principal) {
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
        User createdUser = userRepository.save(User.builder()
                        .firstName(userRequest.firstName())
                        .lastName(userRequest.lastName())
                        .email(userRequest.email())
                        .password(passwordEncoder.encode(userRequest.password()))
                        .gender(Gender.valueOf(userRequest.gender()))
                        .birthDate(userRequest.birthDate())
                        .isEnabled(userRequest.isEnabled())
                        .build());
        return UserDto.map(createdUser);
    }

    @Transactional
    public UserDto updateUser(Long id, UserRequest userRequest) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("User with id %d not found", id)));
        user.setGender(Gender.valueOf(userRequest.gender()));
        user.setEmail(userRequest.email());
        user.setEnabled(userRequest.isEnabled());
        user.setBirthDate(userRequest.birthDate());
        user.setFirstName(userRequest.firstName());
        user.setLastName(userRequest.lastName());
        return UserDto.map(userRepository.save(user));
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("User with id %d not found", id)));
        userRepository.delete(user);
    }

}
