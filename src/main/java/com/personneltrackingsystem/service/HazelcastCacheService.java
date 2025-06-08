package com.personneltrackingsystem.service;

import com.personneltrackingsystem.dto.DtoTurnstileBasedPersonnelEntry;

import java.time.YearMonth;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface HazelcastCacheService {
    
    // Turnstile-based monthly personnel list methods
    void cacheTurnstileBasedMonthlyPersonnelList(YearMonth yearMonth, 
            HashMap<String, Map<String, List<DtoTurnstileBasedPersonnelEntry>>> personnelListByTurnstile);
    
    Optional<HashMap<String, Map<String, List<DtoTurnstileBasedPersonnelEntry>>>> getTurnstileBasedMonthlyPersonnelListFromCache(YearMonth yearMonth);
    
    
    // New method to add daily records to monthly map
    void addDailyRecordsToMonthlyMap(LocalDate date, Map<String, List<DtoTurnstileBasedPersonnelEntry>> dailyRecords);
} 