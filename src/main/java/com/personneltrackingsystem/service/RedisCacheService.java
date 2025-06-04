package com.personneltrackingsystem.service;

import com.personneltrackingsystem.dto.DtoTurnstileBasedPersonnelEntry;

import java.time.YearMonth;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface RedisCacheService {
    
    // Daily turnstile passage records methods
    void addToDailyTurnstilePassageRecord(String turnstileName, DtoTurnstileBasedPersonnelEntry entry, LocalDate recordDate);
    
    Map<String, Map<String, List<DtoTurnstileBasedPersonnelEntry>>> getAllDailyTurnstilePassageRecords();
    
    Map<String, List<DtoTurnstileBasedPersonnelEntry>> getDailyTurnstilePassageRecordsByDate(LocalDate date);
    
    void transferDailyRecordsToMonthlyMap();
    
    void clearDailyTurnstilePassageRecords();
    
    // Turnstile-based monthly personnel list methods
    void cacheTurnstileBasedMonthlyPersonnelList(YearMonth yearMonth, 
            HashMap<String, Map<String, List<DtoTurnstileBasedPersonnelEntry>>> personnelListByTurnstile);
    
    Optional<HashMap<String, Map<String, List<DtoTurnstileBasedPersonnelEntry>>>> getTurnstileBasedMonthlyPersonnelListFromCache(YearMonth yearMonth);
    
    void addDailyRecordsToMonthlyMap(LocalDate date, Map<String, List<DtoTurnstileBasedPersonnelEntry>> dailyRecords);
    
} 