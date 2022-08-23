package com.filip.managementapp.repository;

import com.filip.managementapp.AbstractRepositoryTest;
import com.filip.managementapp.model.Gender;
import com.filip.managementapp.model.Role;
import com.filip.managementapp.model.RoleName;
import com.filip.managementapp.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


class UserRepositoryTest extends AbstractRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private final User user;

    public UserRepositoryTest() {
        this.user = new User(1L,
                "Username",
                "Lastname",
                "test@email.com",
                "$2a$10$x4yGScMFaIArGQS61h814ODoQ9r1qZSaiYjyVyLejI52JoP.EHm6e", // Password1
                Gender.OTHER,
                LocalDate.of(2000, 3, 3),
                true,
                Set.of(new Role(1L, RoleName.ROLE_ADMIN, new HashSet<>()))
        );
    }


    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void shouldFindByEmail() {
        User savedUser = userRepository.save(user);
        Optional<User> foundedUser = userRepository.findByEmail(savedUser.getEmail());

        assertThat(foundedUser).isPresent();
        assertThat(foundedUser.get()).isEqualTo(savedUser);
    }

    @Test
    void shouldNotFindByEmail() {
        userRepository.save(user);
        Optional<User> foundedUser = userRepository.findByEmail("randomEmail123@email.com");

        assertThat(foundedUser).isEmpty();
    }

    @Test
    void shouldExistsByEmail() {
        User savedUser = userRepository.save(user);

        Boolean result = userRepository.existsByEmail(savedUser.getEmail());

        assertTrue(result);
    }

    @Test
    void shouldNotExistsByEmail() {
        User savedUser = userRepository.save(user);

        Boolean result = userRepository.existsByEmail("randomEmail123@email.com");

        assertFalse(result);
    }
}