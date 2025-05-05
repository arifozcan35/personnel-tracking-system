package com.personneltrackingsystem.service;

import com.personneltrackingsystem.dto.TokenPair;
import io.jsonwebtoken.Claims;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.SecretKey;
import java.util.Map;

public interface JwtService {

    TokenPair generateTokenPair(Authentication authentication);

    String generateAccessToken(Authentication authentication);

    String generateRefreshToken(Authentication authentication);

    String generateToken(Authentication authentication, long expirationInMs, Map<String, String> claims);

    boolean validateTokenForUser(String token, UserDetails userDetails);

    boolean isValidToken(String token);

    String extractUsernameFromToken(String token);

    boolean isRefreshToken(String token);

    Claims extractAllClaims(String token);

    SecretKey getSignInKey();
}
