package com.personneltrackingsystem.service;

import com.personneltrackingsystem.dto.DtoTurnstileRegistrationLog;

import java.util.List;
import java.util.Optional;

public interface TurnstileRegistrationLogService {
    List<DtoTurnstileRegistrationLog> getAllLogs();
    Optional<DtoTurnstileRegistrationLog> getLogById(Long id);
    DtoTurnstileRegistrationLog getOneLog(Long id);
    void deleteOneLog(Long id);
} 