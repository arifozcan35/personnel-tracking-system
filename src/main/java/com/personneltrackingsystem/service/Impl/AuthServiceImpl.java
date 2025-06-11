package com.personneltrackingsystem.service.impl;

import com.personneltrackingsystem.dto.LoginRequest;
import com.personneltrackingsystem.dto.RefreshTokenRequest;
import com.personneltrackingsystem.dto.RegisterRequest;
import com.personneltrackingsystem.dto.TokenPair;
import com.personneltrackingsystem.entity.User;
import com.personneltrackingsystem.repository.UserRepository;
import com.personneltrackingsystem.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final JwtServiceImpl jwtService;

    private final UserDetailsService userDetailsService;

    @Override
    @Transactional
    public void registerUser(RegisterRequest registerRequest) {
        if(Boolean.TRUE.equals(userRepository.existsByUsername(registerRequest.getUsername()))) {
            throw new IllegalArgumentException("Username is already in use");
        }

        User user = User
                .builder()
                .name(registerRequest.getName())
                .username(registerRequest.getUsername())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .email(registerRequest.getEmail())
                .role(registerRequest.getRole())
                .build();

        userRepository.save(user);
    }


    @Override
    public TokenPair login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Token Pair
        return jwtService.generateTokenPair(authentication);
    }

    
    @Override
    public TokenPair refreshToken(@Valid RefreshTokenRequest request) {

        String refreshToken = request.getRefreshToken();
        if(!jwtService.isRefreshToken(refreshToken)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        String user = jwtService.extractUsernameFromToken(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(user);

        if (userDetails == null) {
            throw new IllegalArgumentException("User not found");
        }

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

        String accessToken = jwtService.generateAccessToken(authentication);
        return new TokenPair(accessToken, refreshToken);
    }
}
