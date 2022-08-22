package com.filip.managementapp.service;

import com.filip.managementapp.exception.ResourceAlreadyExistsException;
import com.filip.managementapp.exception.ResourceNotFoundException;
import com.filip.managementapp.model.Role;
import com.filip.managementapp.model.RoleName;
import com.filip.managementapp.repository.RoleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleService roleService;

    private final Role role;

    public RoleServiceTest() {
        this.role = new Role(1L, RoleName.ROLE_ADMIN, new HashSet<>());
    }

    @Test
    void willCreateRole() {
        RoleName roleName = RoleName.ROLE_ADMIN;
        Role roleToSave = Role.builder()
                .name(roleName)
                .build();
        given(roleRepository.existsByName(roleName)).willReturn(false);
        given(roleRepository.save(roleToSave)).willReturn(this.role);

        var response = roleService.createRole(roleName);

        assertThat(response).isEqualTo(this.role);
        verify(roleRepository, times(1)).existsByName(roleName);
        verify(roleRepository, times(1)).save(roleToSave);
    }

    @Test
    void createRoleWillThrowResourceAlreadyExistsException() {
        RoleName roleName = RoleName.ROLE_ADMIN;
        given(roleRepository.existsByName(roleName)).willReturn(true);

        assertThatThrownBy(() -> roleService.createRole(roleName))
                .isInstanceOf(ResourceAlreadyExistsException.class)
                .hasMessage(String.format("Role with name %s exists", roleName.name()));

        verify(roleRepository, times(1)).existsByName(roleName);
    }

    @Test
    void willFindRoleByName() {
        RoleName roleName = RoleName.ROLE_ADMIN;
        given(roleRepository.findByName(roleName)).willReturn(Optional.of(this.role));

        Role response = roleService.findRoleByName(roleName);

        assertThat(response).isEqualTo(this.role);
        verify(roleRepository, times(1)).findByName(roleName);
    }

    @Test
    void findRoleByNameWillThrowResourceNotFoundException() {
        RoleName roleName = RoleName.ROLE_USER;
        given(roleRepository.findByName(roleName)).willReturn(Optional.empty());

        assertThatThrownBy(() -> roleService.findRoleByName(roleName))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(String.format("Role with name %s not found", roleName.name()));

        verify(roleRepository, times(1)).findByName(roleName);
    }

    @Test
    void willFindRoleById() {
        Long roleId = 1L;
        given(roleRepository.findById(roleId)).willReturn(Optional.of(this.role));

        Role response = roleService.findRoleById(roleId);

        assertThat(response).isEqualTo(this.role);
        verify(roleRepository, times(1)).findById(roleId);
    }

    @Test
    void findRoleByIdWillThrowResourceNotFoundException() {
        Long roleId = 2L;
        given(roleRepository.findById(roleId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> roleService.findRoleById(roleId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(String.format("Role with id %d not found", roleId));

        verify(roleRepository, times(1)).findById(roleId);
    }

    @Test
    void existsByNameWillReturnTrue() {
        RoleName roleName = RoleName.ROLE_USER;
        given(roleRepository.existsByName(roleName)).willReturn(true);

        Boolean response = roleService.existsByName(roleName);

        assertThat(response).isTrue();
        verify(roleRepository, times(1)).existsByName(roleName);
    }

    @Test
    void existsByNameWillReturnFalse() {
        RoleName roleName = RoleName.ROLE_USER;
        given(roleRepository.existsByName(roleName)).willReturn(false);

        Boolean response = roleService.existsByName(roleName);

        assertThat(response).isFalse();
        verify(roleRepository, times(1)).existsByName(roleName);
    }
}