package com.personneltrackingsystem.controller.impl;

import com.personneltrackingsystem.controller.TurnstileController;
import com.personneltrackingsystem.dto.DtoTurnstile;
import com.personneltrackingsystem.dto.DtoTurnstileIU;
import com.personneltrackingsystem.dto.DtoDailyPersonnelEntry;
import com.personneltrackingsystem.dto.DtoTurnstilePassageRequest;
import com.personneltrackingsystem.service.TurnstileService;
import com.personneltrackingsystem.service.TurnstileRegistrationLogService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class TurnstileControllerImpl implements TurnstileController {

    private final TurnstileService turnstileService;

    private final TurnstileRegistrationLogService turnstileRegistrationLogService;

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

    @Override
    public ResponseEntity<String> passTurnstile(Long turnstileId, DtoTurnstilePassageRequest request, String operationTimeStr) {
        return turnstileService.passTurnstile(turnstileId, request, operationTimeStr);
    }
    
    @Override
    public HashMap<String, List<DtoDailyPersonnelEntry>> getMonthlyMainEntrancePersonnelListWithHazelcast(YearMonth yearMonth) {
        // if no year-month entered, use current month
        YearMonth targetMonth = (yearMonth != null) ? yearMonth : YearMonth.now();
        return turnstileRegistrationLogService.getMonthlyMainEntrancePersonnelList(targetMonth);
    }
    
    @Override
    public HashMap<String, List<DtoDailyPersonnelEntry>> getMonthlyMainEntrancePersonnelListWithRedis(YearMonth yearMonth) {
        // if no year-month entered, use current month
        YearMonth targetMonth = (yearMonth != null) ? yearMonth : YearMonth.now();
        return turnstileRegistrationLogService.getMonthlyMainEntrancePersonnelListWithRedis(targetMonth);
    }
} 