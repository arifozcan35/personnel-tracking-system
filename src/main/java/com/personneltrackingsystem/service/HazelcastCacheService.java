package com.personneltrackingsystem.service;

import com.personneltrackingsystem.dto.DtoDailyPersonnelEntry;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface HazelcastCacheService {

    void cacheDailyPersonnelList(LocalDate date, List<DtoDailyPersonnelEntry> personnelList);
    
    Optional<List<DtoDailyPersonnelEntry>> getDailyPersonnelListFromCache(LocalDate date);
    
    void removeDailyPersonnelListFromCache(LocalDate date);
    
    void clearAllDailyPersonnelCache();
} 