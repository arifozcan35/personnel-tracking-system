package com.personneltrackingsystem.service;

import com.personneltrackingsystem.dto.DtoTurnstile;
import com.personneltrackingsystem.dto.DtoTurnstileIU;

import java.util.List;
import java.util.Optional;

public interface TurnstileService {
    List<DtoTurnstile> getAllTurnstiles();

    Optional<DtoTurnstile> getTurnstileById(Long id);

    DtoTurnstile getOneTurnstile(Long id);

    DtoTurnstile saveOneTurnstile(DtoTurnstile newTurnstile);

    DtoTurnstile updateOneTurnstile(Long id, DtoTurnstileIU newTurnstile);

    void deleteOneTurnstile(Long id);
} 