package com.example.userservice.controller;

import com.example.userservice.dto.UserCommand;
import com.example.userservice.dto.UserRequest;
import com.example.userservice.dto.UserResponse;
import com.example.userservice.dto.UserResult;
import com.example.userservice.model.User;
import com.example.userservice.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAll() {
        return ResponseEntity.ok(
                userService.findAll().stream()
                        .map(this::toResponse)
                        .toList()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(toResponse(userService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<UserResponse> create(@Valid @RequestBody UserRequest request) {
        UserResult result = userService.create(toCommand(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(result));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> update(@PathVariable Long id,
                                               @Valid @RequestBody UserRequest request) {
        return ResponseEntity.ok(toResponse(userService.update(id, toCommand(request))));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/role/{role}")
    public ResponseEntity<List<UserResponse>> getByRole(@PathVariable User.Role role) {
        return ResponseEntity.ok(
                userService.findByRole(role).stream()
                        .map(this::toResponse)
                        .toList()
        );
    }

    @GetMapping("/active")
    public ResponseEntity<List<UserResponse>> getActivos() {
        return ResponseEntity.ok(
                userService.findActivos().stream()
                        .map(this::toResponse)
                        .toList()
        );
    }

    private UserCommand toCommand(UserRequest request) {
        return new UserCommand(
                request.getUsername(),
                request.getEmail(),
                request.getRole()
        );
    }

    private UserResponse toResponse(UserResult result) {
        return new UserResponse(
                result.id(),
                result.username(),
                result.email(),
                result.role(),
                result.active()
        );
    }
}