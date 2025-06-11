package com.personneltrackingsystem.service.cache.impl;

import com.personneltrackingsystem.dto.DtoTurnstileBasedPersonnelEntry;
import com.personneltrackingsystem.service.cache.RedisCacheService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.YearMonth;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisCacheServiceImpl implements RedisCacheService {
    
    @Qualifier("turnstileBasedMonthlyPersonnelRedisTemplate")
    private final RedisTemplate<String, HashMap<String, Map<String, List<DtoTurnstileBasedPersonnelEntry>>>> turnstileBasedMonthlyRedisTemplate;
    
    @Qualifier("dailyTurnstilePassageRedisTemplate")
    private final RedisTemplate<String, Map<String, Map<String, List<DtoTurnstileBasedPersonnelEntry>>>> dailyTurnstilePassageRedisTemplate;
    
    private static final String TURNSTILE_BASED_MONTHLY_PERSONNEL_KEY_PREFIX = "turnstile_based_monthly_personnel:";
    private static final String DAILY_TURNSTILE_PASSAGE_KEY = "daily_turnstile_passage";
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    
    private static final Duration MONTHLY_TTL = Duration.ofDays(7); // 7 days TTL
    
    // Daily turnstile passage records methods
    
    @Override
    public void addToDailyTurnstilePassageRecord(String turnstileName, DtoTurnstileBasedPersonnelEntry entry, LocalDate recordDate) {
        try {
            Map<String, Map<String, List<DtoTurnstileBasedPersonnelEntry>>> allDailyRecords = getAllDailyTurnstilePassageRecords();
            
            if (allDailyRecords == null) {
                allDailyRecords = new ConcurrentHashMap<>();
            }
            
            String dateKey = recordDate.format(DateTimeFormatter.ofPattern(DATE_FORMAT));
            
            // Get records for this date
            Map<String, List<DtoTurnstileBasedPersonnelEntry>> dailyRecords = 
                allDailyRecords.getOrDefault(dateKey, new ConcurrentHashMap<>());
            
            // Get entries for this turnstile
            List<DtoTurnstileBasedPersonnelEntry> turnstileEntries = 
                dailyRecords.getOrDefault(turnstileName, new ArrayList<>());
            
            turnstileEntries.add(entry);
            dailyRecords.put(turnstileName, turnstileEntries);
            allDailyRecords.put(dateKey, dailyRecords);
            
            dailyTurnstilePassageRedisTemplate.opsForValue().set(DAILY_TURNSTILE_PASSAGE_KEY, allDailyRecords);
            
            log.debug("Added turnstile passage record to daily map for turnstile: {} on date: {}", turnstileName, dateKey);
        } catch (Exception e) {
            log.error("Error adding turnstile passage record to daily map for turnstile: {}", turnstileName, e);
        }
    }
    
    @Override
    public Map<String, Map<String, List<DtoTurnstileBasedPersonnelEntry>>> getAllDailyTurnstilePassageRecords() {
        try {
            Map<String, Map<String, List<DtoTurnstileBasedPersonnelEntry>>> allDailyRecords = 
                dailyTurnstilePassageRedisTemplate.opsForValue().get(DAILY_TURNSTILE_PASSAGE_KEY);
            
            return allDailyRecords != null ? allDailyRecords : new ConcurrentHashMap<>();
        } catch (Exception e) {
            log.error("Error retrieving all daily turnstile passage records from Redis", e);
            return new ConcurrentHashMap<>();
        }
    }
    
    @Override
    public Map<String, List<DtoTurnstileBasedPersonnelEntry>> getDailyTurnstilePassageRecordsByDate(LocalDate date) {
        try {
            Map<String, Map<String, List<DtoTurnstileBasedPersonnelEntry>>> allDailyRecords = getAllDailyTurnstilePassageRecords();
            String dateKey = date.format(DateTimeFormatter.ofPattern(DATE_FORMAT));
            
            return allDailyRecords.getOrDefault(dateKey, new ConcurrentHashMap<>());
        } catch (Exception e) {
            log.error("Error retrieving daily turnstile passage records for date: {} from Redis", date, e);
            return new ConcurrentHashMap<>();
        }
    }
    
    @Override
    public void transferDailyRecordsToMonthlyMap() {
        try {
            Map<String, Map<String, List<DtoTurnstileBasedPersonnelEntry>>> allDailyRecords = getAllDailyTurnstilePassageRecords();
            
            if (allDailyRecords == null || allDailyRecords.isEmpty()) {
                log.info("No daily turnstile passage records to transfer");
                return;
            }
            
            // Process each date's records separately
            for (Map.Entry<String, Map<String, List<DtoTurnstileBasedPersonnelEntry>>> dateEntry : allDailyRecords.entrySet()) {
                String dateStr = dateEntry.getKey();
                Map<String, List<DtoTurnstileBasedPersonnelEntry>> dailyRecords = dateEntry.getValue();
                
                // Parse the date
                LocalDate recordDate = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern(DATE_FORMAT));
                
                // Add to monthly map with original date
                addDailyRecordsToMonthlyMap(recordDate, dailyRecords);
                
                log.info("Transferred daily turnstile passage records for date: {} to monthly map", dateStr);
            }
            
            // Clear daily records after successful transfer
            clearDailyTurnstilePassageRecords();
            log.info("Successfully transferred all daily turnstile passage records to monthly maps");
            
        } catch (Exception e) {
            log.error("Error transferring daily records to monthly map", e);
        }
    }
    
    @Override
    public void clearDailyTurnstilePassageRecords() {
        try {
            dailyTurnstilePassageRedisTemplate.delete(DAILY_TURNSTILE_PASSAGE_KEY);
            log.info("Daily turnstile passage records cleared successfully");
        } catch (Exception e) {
            log.error("Error clearing daily turnstile passage records", e);
        }
    }
    
    @Override
    public void addDailyRecordsToMonthlyMap(LocalDate date, Map<String, List<DtoTurnstileBasedPersonnelEntry>> dailyRecords) {
        if (dailyRecords == null || dailyRecords.isEmpty()) {
            log.info("No daily records to add to monthly map for date: {}", date);
            return;
        }
        
        try {
            YearMonth yearMonth = YearMonth.from(date);
            String dateStr = date.format(DateTimeFormatter.ofPattern(DATE_FORMAT));
            
            // Get current monthly map
            Optional<HashMap<String, Map<String, List<DtoTurnstileBasedPersonnelEntry>>>> optMonthlyMap = 
                getTurnstileBasedMonthlyPersonnelListFromCache(yearMonth);
            
            HashMap<String, Map<String, List<DtoTurnstileBasedPersonnelEntry>>> monthlyMap = 
                optMonthlyMap.orElse(new HashMap<>());
            
            // Add daily records to monthly map for this date
            monthlyMap.put(dateStr, dailyRecords);
            
            // Save updated monthly map back to cache
            cacheTurnstileBasedMonthlyPersonnelList(yearMonth, monthlyMap);
            
            log.info("Daily records added to monthly map for date: {}", dateStr);
        } catch (Exception e) {
            log.error("Error adding daily records to monthly map for date: {}", date, e);
        }
    }
    
    // Turnstile-based monthly personnel list methods
    
    @Override
    public void cacheTurnstileBasedMonthlyPersonnelList(YearMonth yearMonth, 
            HashMap<String, Map<String, List<DtoTurnstileBasedPersonnelEntry>>> personnelListByTurnstile) {
        try {
            String cacheKey = generateTurnstileBasedMonthlyKey(yearMonth);
            turnstileBasedMonthlyRedisTemplate.opsForValue().set(cacheKey, personnelListByTurnstile, MONTHLY_TTL);
            
            log.info("Turnstile-based monthly personnel list cached successfully in Redis for month: {}", yearMonth);
        } catch (Exception e) {
            log.error("Error caching turnstile-based monthly personnel list in Redis for month: {}", yearMonth, e);
        }
    }

    @Override
    public Optional<HashMap<String, Map<String, List<DtoTurnstileBasedPersonnelEntry>>>> getTurnstileBasedMonthlyPersonnelListFromCache(YearMonth yearMonth) {
        try {
            String cacheKey = generateTurnstileBasedMonthlyKey(yearMonth);
            HashMap<String, Map<String, List<DtoTurnstileBasedPersonnelEntry>>> cachedMap = 
                turnstileBasedMonthlyRedisTemplate.opsForValue().get(cacheKey);
            
            if (cachedMap != null) {
                log.info("Turnstile-based monthly personnel list retrieved from Redis cache for month: {}", yearMonth);
                return Optional.of(cachedMap);
            } else {
                log.info("Turnstile-based monthly personnel list not found in Redis cache for month: {}", yearMonth);
                return Optional.empty();
            }
        } catch (Exception e) {
            log.error("Error retrieving turnstile-based monthly personnel list from Redis cache for month: {}", yearMonth, e);
            return Optional.empty();
        }
    }
    
    private String generateTurnstileBasedMonthlyKey(YearMonth yearMonth) {
        return TURNSTILE_BASED_MONTHLY_PERSONNEL_KEY_PREFIX + yearMonth.toString();
    }
} 