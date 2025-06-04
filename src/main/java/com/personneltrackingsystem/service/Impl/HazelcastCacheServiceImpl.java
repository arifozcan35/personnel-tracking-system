package com.personneltrackingsystem.service.impl;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import com.personneltrackingsystem.dto.DtoTurnstileBasedPersonnelEntry;
import com.personneltrackingsystem.service.HazelcastCacheService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class HazelcastCacheServiceImpl implements HazelcastCacheService {

    private final HazelcastInstance hazelcastInstance;
    
    private static final String TURNSTILE_BASED_MONTHLY_PERSONNEL_MAP_NAME = "turnstileBasedMonthlyPersonnelList";
    
    // Turnstile-based monthly personnel list methods
    
    @Override
    public void cacheTurnstileBasedMonthlyPersonnelList(YearMonth yearMonth, 
            HashMap<String, Map<String, List<DtoTurnstileBasedPersonnelEntry>>> personnelListByTurnstile) {
        try {
            IMap<String, HashMap<String, Map<String, List<DtoTurnstileBasedPersonnelEntry>>>> turnstileBasedMap = 
                hazelcastInstance.getMap(TURNSTILE_BASED_MONTHLY_PERSONNEL_MAP_NAME);
            
            String cacheKey = generateTurnstileBasedMonthlyKey(yearMonth);
            turnstileBasedMap.put(cacheKey, personnelListByTurnstile);
            
            log.info("Turnstile-based monthly personnel list cached successfully for month: {}", yearMonth);
        } catch (Exception e) {
            log.error("Error caching turnstile-based monthly personnel list for month: {}", yearMonth, e);
        }
    }
    

    @Override
    public Optional<HashMap<String, Map<String, List<DtoTurnstileBasedPersonnelEntry>>>> getTurnstileBasedMonthlyPersonnelListFromCache(YearMonth yearMonth) {
        try {
            IMap<String, HashMap<String, Map<String, List<DtoTurnstileBasedPersonnelEntry>>>> turnstileBasedMap = 
                hazelcastInstance.getMap(TURNSTILE_BASED_MONTHLY_PERSONNEL_MAP_NAME);
            
            String cacheKey = generateTurnstileBasedMonthlyKey(yearMonth);
            HashMap<String, Map<String, List<DtoTurnstileBasedPersonnelEntry>>> cachedList = turnstileBasedMap.get(cacheKey);
            
            if (cachedList != null) {
                log.info("Turnstile-based monthly personnel list retrieved from cache for month: {}", yearMonth);
                return Optional.of(cachedList);
            } else {
                log.info("Turnstile-based monthly personnel list not found in cache for month: {}", yearMonth);
                return Optional.empty();
            }
        } catch (Exception e) {
            log.error("Error retrieving turnstile-based monthly personnel list from cache for month: {}", yearMonth, e);
            return Optional.empty();
        }
    }
    

    @Override
    public void removeTurnstileBasedMonthlyPersonnelListFromCache(YearMonth yearMonth) {
        try {
            IMap<String, HashMap<String, Map<String, List<DtoTurnstileBasedPersonnelEntry>>>> turnstileBasedMap = 
                hazelcastInstance.getMap(TURNSTILE_BASED_MONTHLY_PERSONNEL_MAP_NAME);
            
            String cacheKey = generateTurnstileBasedMonthlyKey(yearMonth);
            turnstileBasedMap.remove(cacheKey);
            
            log.info("Turnstile-based monthly personnel list removed from cache for month: {}", yearMonth);
        } catch (Exception e) {
            log.error("Error removing turnstile-based monthly personnel list from cache for month: {}", yearMonth, e);
        }
    }
    
    
    @Override
    public void clearAllTurnstileBasedMonthlyPersonnelCache() {
        try {
            IMap<String, HashMap<String, Map<String, List<DtoTurnstileBasedPersonnelEntry>>>> turnstileBasedMap = 
                hazelcastInstance.getMap(TURNSTILE_BASED_MONTHLY_PERSONNEL_MAP_NAME);
            
            turnstileBasedMap.clear();
            log.info("All turnstile-based monthly personnel cache cleared successfully");
        } catch (Exception e) {
            log.error("Error clearing all turnstile-based monthly personnel cache", e);
        }
    }
    
    private String generateTurnstileBasedMonthlyKey(YearMonth yearMonth) {
        return "turnstile_based_monthly_personnel_" + yearMonth.toString();
    }
} 