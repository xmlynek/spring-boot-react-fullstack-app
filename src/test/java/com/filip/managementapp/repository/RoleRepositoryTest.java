package com.filip.managementapp.repository;

import com.filip.managementapp.AbstractRepositoryTest;
import com.filip.managementapp.model.Role;
import com.filip.managementapp.model.RoleName;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


class RoleRepositoryTest extends AbstractRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    private final Role role;

    public RoleRepositoryTest() {
        this.role = new Role(1L, RoleName.ROLE_USER, new HashSet<>());
    }

    @AfterEach
    void tearDown() {
        roleRepository.deleteAll();
    }

    @Test
    void shouldFindByName() {
        Role savedRole = roleRepository.save(role);

        Optional<Role> foundedRole = roleRepository.findByName(savedRole.getName());

        assertThat(foundedRole).isPresent();
        assertThat(foundedRole.get()).isEqualTo(savedRole);
    }

    @Test
    void shouldNotFindByName() {
        roleRepository.save(role);

        Optional<Role> foundedRole = roleRepository.findByName(RoleName.ROLE_ADMIN);

        assertThat(foundedRole).isEmpty();
    }

    @Test
    void shouldExistsByName() {
        Role savedRole = roleRepository.save(role);

        Boolean existsByName = roleRepository.existsByName(savedRole.getName());

        assertTrue(existsByName);
    }

    @Test
    void shouldNotExistsByName() {
        roleRepository.save(role);

        Boolean existsByName = roleRepository.existsByName(RoleName.ROLE_ADMIN);

        assertFalse(existsByName);
    }
}