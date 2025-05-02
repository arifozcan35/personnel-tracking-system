package com.personneltrackingsystem.service.Impl;

import com.personneltrackingsystem.entity.Token;
import com.personneltrackingsystem.repository.TokenRepository;
import com.personneltrackingsystem.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final TokenRepository tokenRepository;

    @Override
    public Optional<Token> findByToken(String token) {
        return tokenRepository.findByToken(token);
    }

    @Override
    public void save(Token token) {
        tokenRepository.save(token);
    }
}
