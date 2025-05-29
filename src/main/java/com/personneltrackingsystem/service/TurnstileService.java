package com.personneltrackingsystem.service;

import com.personneltrackingsystem.dto.DtoTurnstile;
import com.personneltrackingsystem.dto.DtoTurnstileIU;
import com.personneltrackingsystem.dto.DtoTurnstilePassageRequest;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;

public interface TurnstileService {
    
    List<DtoTurnstile> getAllTurnstiles();

    Optional<DtoTurnstile> getTurnstileById(Long id);

    DtoTurnstile getOneTurnstile(Long id);

    DtoTurnstile saveOneTurnstile(DtoTurnstileIU newTurnstile);

    DtoTurnstile updateOneTurnstile(Long id, DtoTurnstileIU newTurnstile);

    void deleteOneTurnstile(Long id);


    ResponseEntity<String> passTurnstile(Long turnstileId, DtoTurnstilePassageRequest request, String operationTimeStr);
} 