package com.filip.managementapp.repository;

import com.filip.managementapp.model.Role;
import com.filip.managementapp.model.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(RoleName name);
    boolean existsByName(RoleName name);
}
