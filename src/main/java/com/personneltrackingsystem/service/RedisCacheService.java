package com.personneltrackingsystem.service;

import com.personneltrackingsystem.dto.DtoDailyPersonnelEntry;

import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public interface RedisCacheService {
    
    // Aylık personel listesi metodları
    void cacheMonthlyPersonnelList(YearMonth yearMonth, HashMap<String, List<DtoDailyPersonnelEntry>> personnelListByDay);
    
    Optional<HashMap<String, List<DtoDailyPersonnelEntry>>> getMonthlyPersonnelListFromCache(YearMonth yearMonth);
    
    void removeMonthlyPersonnelListFromCache(YearMonth yearMonth);
    
    void clearAllMonthlyPersonnelCache();
} 