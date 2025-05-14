package com.personneltrackingsystem.controller;

import com.personneltrackingsystem.dto.RegisterRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "User Controller", description = "User CRUD operations")
@RequestMapping("/api/user")
public interface UserController {

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    List<RegisterRequest> getAllUsers();

    @GetMapping("/{userId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    RegisterRequest getOneUser(@PathVariable Long userId);

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    RegisterRequest createUser(@RequestBody RegisterRequest newUser);

    @PutMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    RegisterRequest updateUser(@PathVariable Long userId, @RequestBody RegisterRequest newUser);

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    void deleteUser(@PathVariable Long userId);
} 