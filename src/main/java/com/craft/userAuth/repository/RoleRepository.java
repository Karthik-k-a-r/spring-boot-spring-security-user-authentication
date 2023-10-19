package com.craft.userAuth.repository;

import com.craft.userAuth.enums.Roles;
import com.craft.userAuth.model.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role,Long> {
    Role findByName(Roles role);
}
