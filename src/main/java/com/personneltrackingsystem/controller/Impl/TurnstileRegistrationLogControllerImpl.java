package com.personneltrackingsystem.controller.Impl;

import com.personneltrackingsystem.controller.TurnstileRegistrationLogController;
import com.personneltrackingsystem.dto.DtoTurnstileRegistrationLog;
import com.personneltrackingsystem.dto.DtoTurnstileRegistrationLogIU;
import com.personneltrackingsystem.service.TurnstileRegistrationLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TurnstileRegistrationLogControllerImpl implements TurnstileRegistrationLogController {

    private final TurnstileRegistrationLogService turnstileRegistrationLogService;

    @Override
    public List<DtoTurnstileRegistrationLog> getAllTurnstileRegistrationLogs() {
        return turnstileRegistrationLogService.getAllTurnstileRegistrationLogs();
    }

    @Override
    public DtoTurnstileRegistrationLog getOneTurnstileRegistrationLog(Long logId) {
        return turnstileRegistrationLogService.getOneTurnstileRegistrationLog(logId);
    }

    @Override
    public DtoTurnstileRegistrationLog createTurnstileRegistrationLog(DtoTurnstileRegistrationLogIU newLog) {
        return turnstileRegistrationLogService.createTurnstileRegistrationLog(newLog);
    }

    @Override
    public DtoTurnstileRegistrationLog updateTurnstileRegistrationLog(Long logId, DtoTurnstileRegistrationLogIU newLog) {
        return turnstileRegistrationLogService.updateTurnstileRegistrationLog(logId, newLog);
    }

    @Override
    public void deleteTurnstileRegistrationLog(Long logId) {
        turnstileRegistrationLogService.deleteTurnstileRegistrationLog(logId);
    }
} 