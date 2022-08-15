package com.filip.managementapp.service;

import com.filip.managementapp.model.Role;
import com.filip.managementapp.model.RoleName;
import com.filip.managementapp.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    @Transactional
    public Role createRole(RoleName roleName) {
        if (existsByName(roleName)) {
            throw new EntityExistsException(String.format("Role with name %s exists", roleName.name()));
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
                        () -> new EntityNotFoundException(
                                String.format("Role with name %s not found", roleName.name())
                        )
                );
    }

    @Transactional(readOnly = true)
    public Role findRoleById(Long id) {
        return roleRepository.findById(id)
                .orElseThrow(
                        () -> new EntityNotFoundException(
                                String.format("Role with id %d not found", id)
                        )
                );
    }

    public Boolean existsByName(RoleName roleName) {
        return roleRepository.existsByName(roleName);
    }

    public void deleteRole(Long id) {

    }


}
