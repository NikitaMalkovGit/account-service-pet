package com.pet.accountservice.service;

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
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final Mapper mapper;
    private final EventService eventService;
    private final LoginAttemptService attemptService;

    @Override
    public User loadUserByUsername(String email) throws UsernameNotFoundException {
        String[] split = mapper.splitEmail(email);
        var exception = new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!");
        if (split.length != 2) {
            throw exception;
        }
        String username = split[0];
        String mail = split[1];
        if (!mail.equals(User.MAIL)) {
            attemptService.loginFailed(email);
            throw new BadCredentialsException(email);
        }
        return userRepository.findByUsername(username)
                .orElseThrow(() -> exception);
    }

    public GetUserDto getUserById(long id) {
        return userRepository.findById(id)
                .stream()
                .map(mapper::userToGetUserDto)
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!"));
    }

    public List<GetUserDto> getAllUsers() {
        return userRepository.findAll(Sort.by(Sort.Direction.ASC, "id"))
                .stream()
                .map(mapper::userToGetUserDto)
                .collect(Collectors.toList());
    }

    public GetUserDto grantRole(Role role, String email, User subjectUser) {
        User user = loadUserByUsername(email);
        return this.grantRole(role, user, subjectUser);
    }

    public GetUserDto grantRole(Role role, User user, User subjectUser) {
        if (user.getUserRoleGroup() != role.getGroup()) {
            throw new ValidException("The user cannot combine administrative and business roles!");
        }
        user.addRole(role, roleRepository);
        eventService.log(
                Action.GRANT_ROLE,
                "Grant role " + role + " to " + user.getEmail(),
                subjectUser
        );
        return mapper.userToGetUserDto(userRepository.save(user));
    }

    public GetUserDto removeRole(Role role, String email, User subjectUser) {
        User user = loadUserByUsername(email);
        return this.removeRole(role, user, subjectUser);
    }

    public GetUserDto removeRole(Role role, User user, User subjectUser) {
        Validators.validateRemoveUserRole(user.getRoles(), role);
        user.removeRole(role);
        eventService.log(
                Action.REMOVE_ROLE,
                "Remove role " + role + " from " + user.getEmail(),
                subjectUser
        );
        return mapper.userToGetUserDto(userRepository.save(user));
    }

    @Transactional
    public void deleteUser(String email, User subjectUser) {
        User user = loadUserByUsername(email);
        this.deleteUser(user, subjectUser);
    }

    @Transactional
    public void deleteUser(User user, User subjectUser) {
        if (user.getRoles().contains(Role.ADMINISTRATOR)) {
            throw new ValidException("Can't remove ADMINISTRATOR role!");
        }
        userRepository.deleteByUsername(user.getUsername());
        eventService.log(Action.DELETE_USER, user.getEmail(), subjectUser);
    }

    public void lock(String email, User subjectUser) {
        User user = loadUserByUsername(email);
        this.lock(user, subjectUser);
    }

    public void lock(User user, User subjectUser) {
        if (user.getRoles().contains(Role.ADMINISTRATOR)) {
            throw new ValidException("Can't lock the ADMINISTRATOR!");
        }
        user.setAccountNonLocked(false);
        userRepository.save(user);
        eventService.log(Action.LOCK_USER, "Lock user " + user.getEmail(), subjectUser);
    }

    public void unlock(String email, User subjectUser) {
        User user = loadUserByUsername(email);
        this.unlock(user, subjectUser);
    }

    public void unlock(User user, User subjectUser) {
        user.setAccountNonLocked(true);
        attemptService.clean(user.getEmail());
        userRepository.save(user);
        eventService.log(Action.UNLOCK_USER, "Unlock user " + user.getEmail(), subjectUser);
    }

}
