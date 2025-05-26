package com.personneltrackingsystem.service.Impl;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import com.personneltrackingsystem.dto.DtoDailyPersonnelEntry;
import com.personneltrackingsystem.service.HazelcastCacheService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class HazelcastCacheServiceImpl implements HazelcastCacheService {

    private final HazelcastInstance hazelcastInstance;
    
    private static final String DAILY_PERSONNEL_MAP_NAME = "dailyPersonnelList";

    @Override
    public void cacheDailyPersonnelList(LocalDate date, List<DtoDailyPersonnelEntry> personnelList) {
        try {
            IMap<String, List<DtoDailyPersonnelEntry>> dailyPersonnelMap = 
                hazelcastInstance.getMap(DAILY_PERSONNEL_MAP_NAME);
            
            String cacheKey = generateCacheKey(date);
            dailyPersonnelMap.put(cacheKey, personnelList);
            
            log.info("Daily personnel list cached successfully for date: {}", date);
        } catch (Exception e) {
            log.error("Error caching daily personnel list for date: {}", date, e);
        }
    }

    @Override
    public Optional<List<DtoDailyPersonnelEntry>> getDailyPersonnelListFromCache(LocalDate date) {
        try {
            IMap<String, List<DtoDailyPersonnelEntry>> dailyPersonnelMap = 
                hazelcastInstance.getMap(DAILY_PERSONNEL_MAP_NAME);
            
            String cacheKey = generateCacheKey(date);
            List<DtoDailyPersonnelEntry> cachedList = dailyPersonnelMap.get(cacheKey);
            
            if (cachedList != null) {
                log.info("Daily personnel list retrieved from cache for date: {}", date);
                return Optional.of(cachedList);
            } else {
                log.info("Daily personnel list not found in cache for date: {}", date);
                return Optional.empty();
            }
        } catch (Exception e) {
            log.error("Error retrieving daily personnel list from cache for date: {}", date, e);
            return Optional.empty();
        }
    }

    @Override
    public void removeDailyPersonnelListFromCache(LocalDate date) {
        try {
            IMap<String, List<DtoDailyPersonnelEntry>> dailyPersonnelMap = 
                hazelcastInstance.getMap(DAILY_PERSONNEL_MAP_NAME);
            
            String cacheKey = generateCacheKey(date);
            dailyPersonnelMap.remove(cacheKey);
            
            log.info("Daily personnel list removed from cache for date: {}", date);
        } catch (Exception e) {
            log.error("Error removing daily personnel list from cache for date: {}", date, e);
        }
    }

    @Override
    public void clearAllDailyPersonnelCache() {
        try {
            IMap<String, List<DtoDailyPersonnelEntry>> dailyPersonnelMap = 
                hazelcastInstance.getMap(DAILY_PERSONNEL_MAP_NAME);
            
            dailyPersonnelMap.clear();
            log.info("All daily personnel cache cleared successfully");
        } catch (Exception e) {
            log.error("Error clearing all daily personnel cache", e);
        }
    }

    private String generateCacheKey(LocalDate date) {
        return "daily_personnel_" + date.toString();
    }
} 