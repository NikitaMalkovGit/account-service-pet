package com.pet.accountservice.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetUserDto {

    private Long id;
    private String name;
    private String lastname;
    private String email;
    private Set<String> roles;
    private boolean accountNonLocked;

}
