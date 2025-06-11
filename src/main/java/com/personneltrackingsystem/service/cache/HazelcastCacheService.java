package com.personneltrackingsystem.service.cache;

import com.personneltrackingsystem.dto.DtoTurnstileBasedPersonnelEntry;

import java.time.YearMonth;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface HazelcastCacheService {

    void cacheTurnstileBasedMonthlyPersonnelList(YearMonth yearMonth, 
            HashMap<String, Map<String, List<DtoTurnstileBasedPersonnelEntry>>> personnelListByTurnstile);
    
    Optional<HashMap<String, Map<String, List<DtoTurnstileBasedPersonnelEntry>>>> getTurnstileBasedMonthlyPersonnelListFromCache(YearMonth yearMonth);
    

    void addDailyRecordsToMonthlyMap(LocalDate date, Map<String, List<DtoTurnstileBasedPersonnelEntry>> dailyRecords);
} 