package com.personneltrackingsystem.service;

import com.personneltrackingsystem.entity.Token;

import java.util.Optional;

public interface TokenService {

    Optional<Token> findByToken(String token);

    void save(Token token);
}
