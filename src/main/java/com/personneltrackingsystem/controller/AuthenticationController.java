package com.personneltrackingsystem.controller;

import com.personneltrackingsystem.dto.LoginRequest;
import com.personneltrackingsystem.dto.RefreshTokenRequest;
import com.personneltrackingsystem.dto.RegisterRequest;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

@Tag(name = "Authentication Controller", description = "Authentication operations")
@RequestMapping("/api/auth")
public interface AuthenticationController {

    @PostMapping("/register")
    ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest request);

    @PostMapping("/login")
    ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest);

    @PostMapping("/refresh-token")
    ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshTokenRequest request);

}
