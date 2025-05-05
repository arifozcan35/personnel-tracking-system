package com.personneltrackingsystem.service;

import com.personneltrackingsystem.dto.LoginRequest;
import com.personneltrackingsystem.dto.RefreshTokenRequest;
import com.personneltrackingsystem.dto.RegisterRequest;
import com.personneltrackingsystem.dto.TokenPair;
import jakarta.validation.Valid;

public interface AuthService {

    void registerUser(RegisterRequest registerRequest);

    TokenPair login(LoginRequest loginRequest);

    TokenPair refreshToken(@Valid RefreshTokenRequest request);
}
