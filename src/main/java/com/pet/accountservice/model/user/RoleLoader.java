package com.pet.accountservice.model.user;

import com.pet.accountservice.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Component
class RoleLoader {

    /**
     * This method defines a CommandLineRunner bean that initializes roles in the system if they do not already exist.
     *
     * @param repository The RoleRepository for interacting with role data.
     * @return A CommandLineRunner that loads roles during application startup.
     */
    @Bean
    @Autowired
    CommandLineRunner initRoles(RoleRepository repository) {
        Set<RoleEntity> roles = repository.count() == 0L
                ? Arrays.stream(Role.values())
                .map(role -> RoleEntity.builder().role(role).build())
                .collect(Collectors.toSet())
                : Set.of();
        return args -> repository.saveAll(roles);
    }
}
