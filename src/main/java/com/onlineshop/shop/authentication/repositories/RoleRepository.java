package com.onlineshop.shop.authentication.repositories;

import java.util.Optional;

import com.onlineshop.shop.authentication.models.ERole;
import com.onlineshop.shop.authentication.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);
}
