package com.filip.managementapp.service;

import com.filip.managementapp.repository.UserRepository;
import com.filip.managementapp.model.User;
import com.filip.managementapp.security.MyUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class MyUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        try {
            User user = userRepository
                    .findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException(String.format("User with username '%s' not found", email)));

            return new MyUserDetails(user);

        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }
}
