package com.filip.managementapp.dto;


import com.filip.managementapp.model.Role;
import com.filip.managementapp.model.RoleName;

import lombok.Builder;

@Builder
public record RoleDto(Long id, RoleName name) {

    public static RoleDto map(Role role) {
        return RoleDto.builder()
                .id(role.getId())
                .name(role.getName())
                .build();
    }
}
