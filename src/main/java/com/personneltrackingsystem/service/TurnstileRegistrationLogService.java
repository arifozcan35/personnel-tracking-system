package com.personneltrackingsystem.service;

import com.personneltrackingsystem.dto.DtoTurnstileRegistrationLogIU;
import com.personneltrackingsystem.dto.DtoTurnstileBasedPersonnelEntry;
import com.personneltrackingsystem.entity.OperationType;

import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface TurnstileRegistrationLogService {

    void saveOneTurnstileRegistrationLog(DtoTurnstileRegistrationLogIU dtoTurnstileRegistrationLogIU);

    boolean ifPersonelPassedTurnstile(Long personelId, Long turnstileId);
    
    OperationType getNextOperationType(Long personelId, Long turnstileId);

    // Turnstile-based monthly personnel list methods
    HashMap<String, Map<String, List<DtoTurnstileBasedPersonnelEntry>>> getMonthlyTurnstileBasedPersonnelList(YearMonth yearMonth);
    
    HashMap<String, Map<String, List<DtoTurnstileBasedPersonnelEntry>>> getMonthlyTurnstileBasedPersonnelListFromDatabase(YearMonth yearMonth);
    
    // Validation methods
    YearMonth validateAndGetYearMonth(YearMonth yearMonth);
}
