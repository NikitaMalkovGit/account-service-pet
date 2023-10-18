package com.pet.accountservice.repository;

import com.pet.accountservice.model.user.Role;
import com.pet.accountservice.model.user.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {

    Optional<RoleEntity> findByRole(Role role);
    long count();
}
