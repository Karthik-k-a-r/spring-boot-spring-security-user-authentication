package com.craft.authentication.repository;

import com.craft.authentication.enums.Roles;
import com.craft.authentication.model.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<com.craft.authentication.model.entity.Role,Long> {
    Role findByName(Roles role);
}
