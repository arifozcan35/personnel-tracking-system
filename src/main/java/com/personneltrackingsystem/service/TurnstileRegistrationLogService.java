package com.personneltrackingsystem.service;

import com.personneltrackingsystem.dto.DtoTurnstileRegistrationLogIU;
import com.personneltrackingsystem.dto.DtoDailyPersonnelEntry;
import com.personneltrackingsystem.dto.DtoTurnstileBasedPersonnelEntry;
import com.personneltrackingsystem.entity.OperationType;
import com.personneltrackingsystem.entity.Personel;
import com.personneltrackingsystem.entity.TurnstileRegistrationLog;

import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface TurnstileRegistrationLogService {

    void saveOneTurnstileRegistrationLog(DtoTurnstileRegistrationLogIU dtoTurnstileRegistrationLogIU);

    boolean ifPersonelPassedTurnstile(Long personelId, Long turnstileId);
    
    OperationType getNextOperationType(Long personelId, Long turnstileId);


    // Helper method; used for monthly listing.
    DtoDailyPersonnelEntry createDailyPersonnelEntry(Personel personel, List<TurnstileRegistrationLog> logs);
    
    
    // Monthly personnel list methods
    HashMap<String, List<DtoDailyPersonnelEntry>> getMonthlyMainEntrancePersonnelList(YearMonth yearMonth);
    
    HashMap<String, List<DtoDailyPersonnelEntry>> getMonthlyMainEntrancePersonnelListFromDatabase(YearMonth yearMonth);
    

    // Turnstile-based monthly personnel list methods
    HashMap<String, Map<String, List<DtoTurnstileBasedPersonnelEntry>>> getMonthlyTurnstileBasedPersonnelList(YearMonth yearMonth);
    
    HashMap<String, Map<String, List<DtoTurnstileBasedPersonnelEntry>>> getMonthlyTurnstileBasedPersonnelListFromDatabase(YearMonth yearMonth);
    

    // Validation methods
    YearMonth validateAndGetYearMonth(YearMonth yearMonth);
}
