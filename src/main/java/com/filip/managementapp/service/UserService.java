package com.filip.managementapp.service;

import com.filip.managementapp.dto.UserDto;
import com.filip.managementapp.exception.ResourceNotFoundException;
import com.filip.managementapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public UserDto getCurrentlyLoggedUser(Principal principal) {
        return UserDto.map(userRepository.findByEmail(principal.getName())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found")));
    }

}
