package com.personneltrackingsystem.service.impl;

import org.springframework.stereotype.Service;

import com.personneltrackingsystem.dto.DtoTurnstileRegistrationLogIU;
import com.personneltrackingsystem.dto.DtoTurnstileBasedPersonnelEntry;
import com.personneltrackingsystem.entity.OperationType;
import com.personneltrackingsystem.entity.Personel;
import com.personneltrackingsystem.entity.TurnstileRegistrationLog;
import com.personneltrackingsystem.exception.ValidationException;
import com.personneltrackingsystem.exception.MessageType;
import com.personneltrackingsystem.mapper.TurnstileRegistrationLogMapper;
import com.personneltrackingsystem.repository.TurnstileRegistrationLogRepository;
import com.personneltrackingsystem.service.TurnstileRegistrationLogService;
import com.personneltrackingsystem.service.PersonelService;
import com.personneltrackingsystem.service.HazelcastCacheService;
import com.personneltrackingsystem.service.RedisCacheService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
@Service
@RequiredArgsConstructor
public class TurnstileRegistrationLogServiceImpl implements TurnstileRegistrationLogService {

    private final TurnstileRegistrationLogRepository turnstileRegistrationLogRepository;

    private final TurnstileRegistrationLogMapper turnstileRegistrationLogMapper;

    private final PersonelService personelService;

    private final HazelcastCacheService hazelcastCacheService;

    private final RedisCacheService redisCacheService;

    // Cache refresh task that only runs at the beginning of the day (midnight 00:00)
    @Scheduled(cron = "0 0 0 * * *") // At midnight every day
    public void dailyCacheRefresh() {
        log.info("Daily cache refresh process started - midnight");
        YearMonth currentMonth = YearMonth.now();
        
        try {
            // First transfer daily records to monthly map
            redisCacheService.transferDailyRecordsToMonthlyMap();
            
            // Clear Hazelcast cache
            hazelcastCacheService.clearAllTurnstileBasedMonthlyPersonnelCache();
            
            log.info("Daily cache refresh process completed successfully");
        } catch (Exception e) {
            log.error("Error during daily cache refresh", e);
        }
    }
    
    private void clearAllCaches() {
        try {
            hazelcastCacheService.clearAllTurnstileBasedMonthlyPersonnelCache();
            log.info("Hazelcast cache cleared successfully");
        } catch (Exception e) {
            log.error("Error clearing Hazelcast cache", e);
        }
    }
    
    @Override
    public void saveOneTurnstileRegistrationLog(DtoTurnstileRegistrationLogIU dtoTurnstileRegistrationLogIU){
        TurnstileRegistrationLog turnstileRegistrationLog = turnstileRegistrationLogMapper.dtoTurnstileRegistrationLogIUToTurnstileRegistrationLog(dtoTurnstileRegistrationLogIU);
        turnstileRegistrationLogRepository.save(turnstileRegistrationLog);
        
        // Cache refresh is automatically done daily at midnight
    }


    @Override
    public boolean ifPersonelPassedTurnstile(Long personelId, Long turnstileId){
        return turnstileRegistrationLogRepository.passedTurnstile(personelId, turnstileId);
    }


    @Override
    public OperationType getNextOperationType(Long personelId, Long turnstileId) {
        java.util.List<String> operationTypes = turnstileRegistrationLogRepository.findOperationTypesByPersonelAndTurnstile(personelId, turnstileId);
        
        // if there are no previous records or the last operation was OUT, the next should be IN
        if (operationTypes == null || operationTypes.isEmpty() || OperationType.OUT.getValue().equals(operationTypes.get(0))) {
            return OperationType.IN;
        } else {
            // if the last operation was IN, the next should be OUT
            return OperationType.OUT;
        }
    }
    
    // Turnstile-based monthly personnel list methods
    @Override
    public HashMap<String, Map<String, List<DtoTurnstileBasedPersonnelEntry>>> getMonthlyTurnstileBasedPersonnelList(YearMonth yearMonth) {
        // First try to get from hazelcast cache
        Optional<HashMap<String, Map<String, List<DtoTurnstileBasedPersonnelEntry>>>> cachedResult = 
            hazelcastCacheService.getTurnstileBasedMonthlyPersonnelListFromCache(yearMonth);
            
        if (cachedResult.isPresent()) {
            log.info("Turnstile-based monthly personnel list retrieved from Hazelcast cache: {}", yearMonth);
            return cachedResult.get();
        }
        
        // If not in Hazelcast cache, get from Redis
        cachedResult = redisCacheService.getTurnstileBasedMonthlyPersonnelListFromCache(yearMonth);
        
        if (cachedResult.isPresent()) {
            log.info("Turnstile-based monthly personnel list retrieved from Redis: {}", yearMonth);
            // Also cache in Hazelcast for faster future access
            hazelcastCacheService.cacheTurnstileBasedMonthlyPersonnelList(yearMonth, cachedResult.get());
            return cachedResult.get();
        }
        
        // If not found in Redis, return empty map
        log.info("Turnstile-based monthly personnel list not found in Redis for month: {}", yearMonth);
        return new HashMap<>();
    }
    
    @Override
    public HashMap<String, Map<String, List<DtoTurnstileBasedPersonnelEntry>>> getMonthlyTurnstileBasedPersonnelListFromDatabase(YearMonth yearMonth) {
        // This method is now deprecated as we're using only Redis-stored records
        // But we'll keep it for backward compatibility
        log.warn("Database fetch for monthly turnstile records is deprecated. Use Redis cache instead.");
        
        // Get all turnstile logs for the specified month from ALL turnstiles, not just main entrances
        List<TurnstileRegistrationLog> allMonthLogs = turnstileRegistrationLogRepository.findAllLogsByMonth(
            yearMonth.getYear(), yearMonth.getMonthValue());
        
        if (allMonthLogs.isEmpty()) {
            return new HashMap<>();
        }
        
        // Group logs by date
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Map<String, List<TurnstileRegistrationLog>> logsByDate = allMonthLogs.stream()
            .collect(Collectors.groupingBy(log -> log.getOperationTime().format(dateFormatter)));
        
        // Create result map: date -> (turnstile name -> list of personnel entries)
        HashMap<String, Map<String, List<DtoTurnstileBasedPersonnelEntry>>> result = new HashMap<>();
        
        // Process each date
        for (Map.Entry<String, List<TurnstileRegistrationLog>> dateEntry : logsByDate.entrySet()) {
            String dateStr = dateEntry.getKey();
            List<TurnstileRegistrationLog> logsForDate = dateEntry.getValue();
            
            // Group logs by turnstile for this date
            Map<String, List<TurnstileRegistrationLog>> logsByTurnstile = logsForDate.stream()
                .collect(Collectors.groupingBy(log -> log.getTurnstileId().getTurnstileName()));
            
            Map<String, List<DtoTurnstileBasedPersonnelEntry>> turnstileEntries = new HashMap<>();
            
            // Process each turnstile's logs
            for (Map.Entry<String, List<TurnstileRegistrationLog>> turnstileEntry : logsByTurnstile.entrySet()) {
                String turnstileName = turnstileEntry.getKey();
                List<TurnstileRegistrationLog> turnstileLogs = turnstileEntry.getValue();
                
                // Convert logs to personnel entries
                List<DtoTurnstileBasedPersonnelEntry> personnelEntries = turnstileLogs.stream()
                    .map(this::createTurnstileBasedPersonnelEntry)
                    .collect(Collectors.toList());
                
                // Add to turnstile entries map
                turnstileEntries.put(turnstileName, personnelEntries);
            }
            
            // Add to result map
            result.put(dateStr, turnstileEntries);
        }
        
        return result;
    }
    
    private DtoTurnstileBasedPersonnelEntry createTurnstileBasedPersonnelEntry(TurnstileRegistrationLog log) {
        Personel personel = log.getPersonelId();
        
        DtoTurnstileBasedPersonnelEntry entry = new DtoTurnstileBasedPersonnelEntry();
        entry.setPersonelId(personel.getPersonelId());
        entry.setPersonelName(personel.getName());
        entry.setPersonelEmail(personel.getEmail());
        entry.setOperationTime(log.getOperationTime());
        entry.setOperationType(log.getOperationType());
        
        return entry;
    }
    
    @Override
    public YearMonth validateAndGetYearMonth(YearMonth yearMonth) {
        if (yearMonth != null) {
            try {
                // YearMonth is already parsed by Spring's @DateTimeFormat, 
                // but we need to ensure it's valid
                String yearMonthStr = yearMonth.toString();
                YearMonth.parse(yearMonthStr);
                log.info("Valid year-month format received: {}", yearMonthStr);
                return yearMonth;
            } catch (DateTimeParseException e) {
                log.error("Invalid year-month format received: {}", yearMonth, e);
                throw new ValidationException(MessageType.INVALID_DATE_FORMAT);
            }
        } else {
            return YearMonth.now();
        }
    }
}
