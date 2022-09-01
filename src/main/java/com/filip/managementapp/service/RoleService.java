package com.filip.managementapp.service;

import com.filip.managementapp.exception.ResourceAlreadyExistsException;
import com.filip.managementapp.exception.ResourceNotFoundException;
import com.filip.managementapp.model.Role;
import com.filip.managementapp.model.RoleName;
import com.filip.managementapp.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    @Transactional
    public Role createRole(RoleName roleName) {
        if (existsByName(roleName)) {
            throw new ResourceAlreadyExistsException(String.format("Role with name %s exists", roleName.name()));
        }
        return roleRepository.save(
                Role.builder()
                .name(roleName)
                .build()
        );
    }

    @Transactional(readOnly = true)
    public Role findRoleByName(RoleName roleName) {
        return roleRepository.findByName(roleName)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                String.format("Role with name %s not found", roleName.name())
                        )
                );
    }

    @Transactional(readOnly = true)
    public Role findRoleById(Long id) {
        return roleRepository.findById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                String.format("Role with id %d not found", id)
                        )
                );
    }

    @Transactional(readOnly = true)
    public Boolean existsByName(RoleName roleName) {
        return roleRepository.existsByName(roleName);
    }

    @Transactional
    public Set<Role> getIfExistsByNameOrCreateRoles(RoleName... roleNames) {
        return Arrays.stream(roleNames)
                .map(roleName -> existsByName(roleName)
                        ? findRoleByName(roleName)
                        : createRole(roleName))
                .collect(Collectors.toSet());
    }

}
