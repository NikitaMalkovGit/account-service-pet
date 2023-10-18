package com.pet.accountservice.controller;

import com.pet.accountservice.dto.user.GetUserDto;
import com.pet.accountservice.dto.user.UpdateLockUserDto;
import com.pet.accountservice.dto.user.UpdateRoleUserDto;
import com.pet.accountservice.model.user.Role;
import com.pet.accountservice.model.user.User;
import com.pet.accountservice.service.UserService;
import com.pet.accountservice.util.AppUtils;
import com.pet.accountservice.util.ResponseStatus;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


import javax.validation.Valid;
import java.util.List;


@AllArgsConstructor
@RestController
@RequestMapping("api/admin")
public class AdminController {

    private final UserService userService;

    @PutMapping("user/role")
    public GetUserDto updateUserRole(@Valid @RequestBody UpdateRoleUserDto dto,
                                     @AuthenticationPrincipal User user) {
        Role role = AppUtils.valueOf(Role.class, dto.getRole());
        UpdateRoleUserDto.Operation operation =
                AppUtils.valueOf(UpdateRoleUserDto.Operation.class, dto.getOperation());
        switch (operation) {
            case GRANT:
                return userService.grantRole(role, dto.getUser(), user);
            case REMOVE:
                return userService.removeRole(role, dto.getUser(), user);
            default:
                throw new IllegalStateException();
        }
    }

    @DeleteMapping("user/{username}")
    public ResponseEntity<?> deleteUser(@PathVariable(name = "username") String email,
                                        @AuthenticationPrincipal User user) {
        userService.deleteUser(email, user);
        return ResponseStatus.builder()
                .add("status", "Deleted successfully!")
                .add("user", email.toLowerCase())
                .build();
    }

    @GetMapping("user")
    public List<GetUserDto> getUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("user/{id}")
    public GetUserDto getUser(@PathVariable long id) {
        return userService.getUserById(id);
    }

    @PutMapping("user/access")
    public ResponseEntity<?> lockOrUnlock(@Valid @RequestBody UpdateLockUserDto dto,
                                          @AuthenticationPrincipal User user) {
        UpdateLockUserDto.Operation operation =
                AppUtils.valueOf(UpdateLockUserDto.Operation.class, dto.getOperation());
        switch (operation) {
            case LOCK:
                userService.lock(dto.getUser(), user);
                return ResponseStatus.builder()
                        .add("status", "User " + dto.getUser().toLowerCase() + " locked!")
                        .build();
            case UNLOCK:
                userService.unlock(dto.getUser(), user);
                return ResponseStatus.builder()
                        .add("status", "User " + dto.getUser().toLowerCase() + " unlocked!")
                        .build();
            default:
                throw new IllegalStateException();
        }
    }

}
