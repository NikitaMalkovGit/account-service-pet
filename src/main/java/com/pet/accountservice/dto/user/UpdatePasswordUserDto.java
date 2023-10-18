package com.pet.accountservice.dto.user;

import com.pet.accountservice.validator.BreachedPassword;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdatePasswordUserDto {

    @NotNull(message = "New password cannot be null")
    @NotBlank(message = "New password cannot be blank")
    @Size(min = 12, message = "Password length must be 12 chars minimum!")
    @BreachedPassword
    private String newPassword;
}
