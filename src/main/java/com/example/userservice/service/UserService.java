package com.example.userservice.service;

import com.example.userservice.dto.UserCommand;
import com.example.userservice.dto.UserResult;
import com.example.userservice.model.User;
import com.example.userservice.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserResult> findAll() {
        return userRepository.findAll().stream()
                .map(this::toResult)
                .toList();
    }

    public UserResult findById(Long id) {
        return userRepository.findById(id)
                .map(this::toResult)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Usuario no encontrado con id: " + id));
    }

    public UserResult create(UserCommand command) {
        if (userRepository.existsByUsername(command.username())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "El username ya está en uso: " + command.username());
        }
        if (userRepository.existsByEmail(command.email())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "El email ya está registrado: " + command.email());
        }
        User user = new User();
        user.setUsername(command.username());
        user.setEmail(command.email());
        user.setRole(User.Role.valueOf(command.role()));
        user.setActive(true);
        return toResult(userRepository.save(user));
    }

    public UserResult update(Long id, UserCommand command) {
        User existente = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Usuario no encontrado con id: " + id));
        existente.setUsername(command.username());
        existente.setEmail(command.email());
        existente.setRole(User.Role.valueOf(command.role()));
        return toResult(userRepository.save(existente));
    }

    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Usuario no encontrado con id: " + id);
        }
        userRepository.deleteById(id);
    }

    public List<UserResult> findByRole(User.Role role) {
        return userRepository.findByRole(role).stream()
                .map(this::toResult)
                .toList();
    }

    public List<UserResult> findActivos() {
        return userRepository.findByActiveTrue().stream()
                .map(this::toResult)
                .toList();
    }

    private UserResult toResult(User user) {
        return new UserResult(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().name(),
                user.isActive()
        );
    }
}