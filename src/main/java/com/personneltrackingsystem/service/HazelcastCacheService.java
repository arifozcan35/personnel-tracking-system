package com.personneltrackingsystem.service;

import com.personneltrackingsystem.dto.DtoDailyPersonnelEntry;
import com.personneltrackingsystem.dto.DtoTurnstileBasedPersonnelEntry;

import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface HazelcastCacheService {
    
    // Aylık personel listesi metodları
    void cacheMonthlyPersonnelList(YearMonth yearMonth, HashMap<String, List<DtoDailyPersonnelEntry>> personnelListByDay);
    
    Optional<HashMap<String, List<DtoDailyPersonnelEntry>>> getMonthlyPersonnelListFromCache(YearMonth yearMonth);
    
    void removeMonthlyPersonnelListFromCache(YearMonth yearMonth);
    
    void clearAllMonthlyPersonnelCache();
    
    // Turnike bazlı aylık personel listesi metodları
    void cacheTurnstileBasedMonthlyPersonnelList(YearMonth yearMonth, 
            HashMap<String, Map<String, List<DtoTurnstileBasedPersonnelEntry>>> personnelListByTurnstile);
    
    Optional<HashMap<String, Map<String, List<DtoTurnstileBasedPersonnelEntry>>>> getTurnstileBasedMonthlyPersonnelListFromCache(YearMonth yearMonth);
    
    void removeTurnstileBasedMonthlyPersonnelListFromCache(YearMonth yearMonth);
    
    void clearAllTurnstileBasedMonthlyPersonnelCache();
} 