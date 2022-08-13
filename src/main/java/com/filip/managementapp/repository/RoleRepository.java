package com.filip.managementapp.repository;

import com.filip.managementapp.model.Role;
import com.filip.managementapp.model.RoleNames;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(RoleNames name);
}
