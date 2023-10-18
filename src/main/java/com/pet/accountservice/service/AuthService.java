package com.pet.accountservice.service;

import com.pet.accountservice.dto.user.CreateUserDto;
import com.pet.accountservice.dto.user.GetUserDto;
import com.pet.accountservice.exception.ValidException;
import com.pet.accountservice.mapper.Mapper;
import com.pet.accountservice.model.event.Action;
import com.pet.accountservice.model.user.Role;
import com.pet.accountservice.model.user.User;
import com.pet.accountservice.repository.RoleRepository;
import com.pet.accountservice.repository.UserRepository;
import com.pet.accountservice.validator.Validators;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@AllArgsConstructor
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final Mapper mapper;
    private final EventService eventService;

    public GetUserDto register(CreateUserDto dto) {
        String username = mapper.emailToUsername(dto.getEmail());
        if (userRepository.findByUsername(username).isPresent()) {
            throw new ValidException("User exist!");
        }
        String encodedPassword = passwordEncoder.encode(dto.getPassword());
        Role role = userRepository.count() == 0L
                ? Role.ADMINISTRATOR
                : Role.USER;
        User user = User.builder()
                .name(dto.getName())
                .lastname(dto.getLastname())
                .username(username)
                .password(encodedPassword)
                .roles(Set.of(role), roleRepository)
                .build();
        eventService.log(Action.CREATE_USER, user.getEmail(), (String) null);
        return mapper.userToGetUserDto(userRepository.save(user));
    }

    @Transactional
    public User changePassword(User user, String password) {
        Validators.validatePasswordSame(password, user, passwordEncoder);
        user.setPassword(passwordEncoder.encode(password));
        eventService.log(Action.CHANGE_PASSWORD, user.getEmail(), user);
        return userRepository.save(user);
    }

    public GetUserDto getCurrentUser(User user) {
        return mapper.userToGetUserDto(user);
    }

}
