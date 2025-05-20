package com.personneltrackingsystem.controller.Impl;

import com.personneltrackingsystem.controller.TurnstileController;
import com.personneltrackingsystem.dto.DtoTurnstile;
import com.personneltrackingsystem.dto.DtoTurnstileIU;
import com.personneltrackingsystem.service.TurnstileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TurnstileControllerImpl implements TurnstileController {

    private final TurnstileService turnstileService;

    @Override
    public List<DtoTurnstile> getAllTurnstiles() {
        return turnstileService.getAllTurnstiles();
    }

    @Override
    public DtoTurnstile getOneTurnstile(Long turnstileId) {
        return turnstileService.getOneTurnstile(turnstileId);
    }

    @Override
    public DtoTurnstile createTurnstile(DtoTurnstileIU newTurnstile) {
        return turnstileService.saveOneTurnstile(newTurnstile);
    }

    @Override
    public DtoTurnstile updateTurnstile(Long turnstileId, DtoTurnstileIU newTurnstile) {
        return turnstileService.updateOneTurnstile(turnstileId, newTurnstile);
    }

    @Override
    public void deleteTurnstile(Long turnstileId) {
        turnstileService.deleteOneTurnstile(turnstileId);
    }
} 