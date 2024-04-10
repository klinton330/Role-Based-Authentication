package com.test.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.test.model.ERole;
import com.test.model.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
       public Optional<Role>findByName(ERole erole);
}
