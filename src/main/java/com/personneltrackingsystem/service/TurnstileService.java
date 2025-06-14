package com.personneltrackingsystem.service;

import com.personneltrackingsystem.dto.DtoTurnstile;
import com.personneltrackingsystem.dto.DtoTurnstileIU;
import com.personneltrackingsystem.dto.DtoTurnstilePassageFullRequest;
import com.personneltrackingsystem.dto.DtoTurnstileBasedPersonnelEntry;

import java.util.List;
import java.util.Optional;
import java.util.Map;

import org.springframework.http.ResponseEntity;

public interface TurnstileService {
    
    List<DtoTurnstile> getAllTurnstiles();

    Optional<DtoTurnstile> getTurnstileById(Long id);

    DtoTurnstile getOneTurnstile(Long id);

    DtoTurnstile saveOneTurnstile(DtoTurnstileIU newTurnstile);

    DtoTurnstile updateOneTurnstile(Long id, DtoTurnstileIU newTurnstile);

    void deleteOneTurnstile(Long id);

    ResponseEntity<String> passTurnstile(DtoTurnstilePassageFullRequest request);

    Map<String, List<DtoTurnstileBasedPersonnelEntry>> getDailyTurnstilePassageRecords();
} 