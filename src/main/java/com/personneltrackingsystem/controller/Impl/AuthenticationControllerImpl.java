package com.personneltrackingsystem.controller.impl;

import com.personneltrackingsystem.controller.AuthenticationController;
import com.personneltrackingsystem.dto.LoginRequest;
import com.personneltrackingsystem.dto.RefreshTokenRequest;
import com.personneltrackingsystem.dto.RegisterRequest;
import com.personneltrackingsystem.dto.TokenPair;
import com.personneltrackingsystem.service.impl.AuthServiceImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthenticationControllerImpl implements AuthenticationController {

    private final AuthServiceImpl authService;


    @Override
    public ResponseEntity<?> registerUser(RegisterRequest request) {
        // Save the new user to the database and return success response
        authService.registerUser(request);
        return ResponseEntity.ok("User registered successfully");
    }

    @Override
    public ResponseEntity<?> login(LoginRequest loginRequest) {
        TokenPair tokenPair = authService.login(loginRequest);
        return ResponseEntity.ok(tokenPair);
    }

    @Override
    public ResponseEntity<?> refreshToken(RefreshTokenRequest request) {
        TokenPair tokenPair = authService.refreshToken(request);
        return ResponseEntity.ok(tokenPair);
    }

}
