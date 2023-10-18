package com.pet.accountservice.model.user;

import com.pet.accountservice.repository.RoleRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

@AllArgsConstructor
@Getter
public enum Role implements GrantedAuthority {

    USER(RoleGroup.BUSINESS),
    ACCOUNTANT(RoleGroup.BUSINESS),
    ADMINISTRATOR(RoleGroup.ADMINISTRATIVE),
    AUDITOR(RoleGroup.BUSINESS);

    private final RoleGroup group;

    RoleEntity toRoleEntity(RoleRepository repository) {
        return repository.findByRole(this).orElseThrow();
    }

    @Override
    public String getAuthority() {
        return "ROLE_" + name();
    }

    @Override
    public String toString() {
        return name();
    }

}
