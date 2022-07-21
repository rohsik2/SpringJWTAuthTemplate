package com.restAPI.restAPI.user.repository;

import com.restAPI.restAPI.user.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepo extends JpaRepository<Role, Long> {
    Role findByName(String name);
}
