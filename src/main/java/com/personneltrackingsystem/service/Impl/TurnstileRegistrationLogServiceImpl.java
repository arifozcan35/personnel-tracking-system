package com.personneltrackingsystem.service.impl;

import org.springframework.stereotype.Service;

import com.personneltrackingsystem.dto.DtoTurnstileRegistrationLogIU;
import com.personneltrackingsystem.dto.DtoTurnstileBasedPersonnelEntry;
import com.personneltrackingsystem.dto.DtoTurnstilePassageFullRequest;
import com.personneltrackingsystem.entity.OperationType;
import com.personneltrackingsystem.entity.TurnstileRegistrationLog;
import com.personneltrackingsystem.exception.ValidationException;
import com.personneltrackingsystem.exception.MessageType;
import com.personneltrackingsystem.mapper.TurnstileRegistrationLogMapper;
import com.personneltrackingsystem.repository.TurnstileRegistrationLogRepository;
import com.personneltrackingsystem.service.TurnstileRegistrationLogService;
import com.personneltrackingsystem.service.HazelcastCacheService;
import com.personneltrackingsystem.service.RedisCacheService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TurnstileRegistrationLogServiceImpl implements TurnstileRegistrationLogService {

    private final TurnstileRegistrationLogRepository turnstileRegistrationLogRepository;

    private final TurnstileRegistrationLogMapper turnstileRegistrationLogMapper;

    private final HazelcastCacheService hazelcastCacheService;

    private final RedisCacheService redisCacheService;
    

    // In-memory cache for tracking latest operations
    // Key: "personelId:turnstileId", Value: OperationType
    private final ConcurrentHashMap<String, OperationType> latestOperationCache = new ConcurrentHashMap<>();


    // Cache refresh task that only runs at the beginning of the day (midnight 00:00)
    @Scheduled(cron = "0 0 0 * * *") // At midnight every day
    public void dailyCacheRefresh() {
        log.info("Daily cache refresh process started - midnight");
        
        try {
            // Get all daily records before transferring
            Map<String, Map<String, List<DtoTurnstileBasedPersonnelEntry>>> allDailyRecords = 
                redisCacheService.getAllDailyTurnstilePassageRecords();
                
            // If there are daily records to transfer
            if (allDailyRecords != null && !allDailyRecords.isEmpty()) {
                // Process each date's records separately before clearing
                for (Map.Entry<String, Map<String, List<DtoTurnstileBasedPersonnelEntry>>> dateEntry : allDailyRecords.entrySet()) {
                    String dateStr = dateEntry.getKey();
                    Map<String, List<DtoTurnstileBasedPersonnelEntry>> dailyRecords = dateEntry.getValue();
                    
                    // Parse the date
                    LocalDate recordDate = LocalDate.parse(dateStr, 
                        java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    
                    // Add to Hazelcast monthly map with original date
                    hazelcastCacheService.addDailyRecordsToMonthlyMap(recordDate, dailyRecords);
                    
                    log.info("Transferred daily turnstile passage records for date: {} to Hazelcast monthly map", dateStr);
                }
            }
            
            // Transfer daily records to Redis monthly map
            redisCacheService.transferDailyRecordsToMonthlyMap();
            
            // Clear the in-memory operation cache at midnight
            latestOperationCache.clear();
            
            log.info("Daily cache refresh process completed successfully");
        } catch (Exception e) {
            log.error("Error during daily cache refresh", e);
        }
    }
    
    
    @Override
    @Transactional
    public void saveOneTurnstileRegistrationLog(DtoTurnstileRegistrationLogIU dtoTurnstileRegistrationLogIU){
        TurnstileRegistrationLog turnstileRegistrationLog = turnstileRegistrationLogMapper.dtoTurnstileRegistrationLogIUToTurnstileRegistrationLog(dtoTurnstileRegistrationLogIU);
        turnstileRegistrationLogRepository.save(turnstileRegistrationLog);
        
        // also add to in-memory cache
        String cacheKey = dtoTurnstileRegistrationLogIU.getPersonelId() + ":" + dtoTurnstileRegistrationLogIU.getTurnstileId();
        latestOperationCache.put(cacheKey, dtoTurnstileRegistrationLogIU.getOperationType());
        
        log.info("Saved turnstile registration log and updated cache: {} - {}", cacheKey, dtoTurnstileRegistrationLogIU.getOperationType());
        
        // Cache refresh is automatically done daily at midnight
    }

    
    @Override
    public HashMap<String, Map<String, List<DtoTurnstileBasedPersonnelEntry>>> getMonthlyTurnstileBasedPersonnelListFromHazelcast(YearMonth yearMonth) {
        // Get data exclusively from Hazelcast
        Optional<HashMap<String, Map<String, List<DtoTurnstileBasedPersonnelEntry>>>> cachedResult = 
            hazelcastCacheService.getTurnstileBasedMonthlyPersonnelListFromCache(yearMonth);
            
        if (cachedResult.isPresent()) {
            log.info("Turnstile-based monthly personnel list retrieved from Hazelcast cache: {}", yearMonth);
            return cachedResult.get();
        }
        
        // If not found in Hazelcast, return empty map
        log.info("Turnstile-based monthly personnel list not found in Hazelcast for month: {}", yearMonth);
        return new HashMap<>();
    }
    
    @Override
    public HashMap<String, Map<String, List<DtoTurnstileBasedPersonnelEntry>>> getMonthlyTurnstileBasedPersonnelListFromRedis(YearMonth yearMonth) {
        // Get data exclusively from Redis
        Optional<HashMap<String, Map<String, List<DtoTurnstileBasedPersonnelEntry>>>> cachedResult = 
            redisCacheService.getTurnstileBasedMonthlyPersonnelListFromCache(yearMonth);
        
        if (cachedResult.isPresent()) {
            log.info("Turnstile-based monthly personnel list retrieved from Redis: {}", yearMonth);
            return cachedResult.get();
        }
        
        // If not found in Redis, return empty map
        log.info("Turnstile-based monthly personnel list not found in Redis for month: {}", yearMonth);
        return new HashMap<>();
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

    @Override
    public void validateTurnstilePassage(DtoTurnstilePassageFullRequest request) {
        Long personelId = request.getPersonelId();
        Long turnstileId = request.getWantedToEnterTurnstileId();
        OperationType operationType = request.getOperationType();
        
        // first check the cache for the last operation
        String cacheKey = personelId + ":" + turnstileId;
        OperationType lastOperation = latestOperationCache.get(cacheKey);
        
        // if there is no record in the cache, check the database
        if (lastOperation == null) {
            List<String> operationTypes = turnstileRegistrationLogRepository.findOperationTypesByPersonelAndTurnstile(personelId, turnstileId);
            
            if (!operationTypes.isEmpty()) {
                try {
                    lastOperation = OperationType.valueOf(operationTypes.get(0));
                    // add the last operation to the cache
                    latestOperationCache.put(cacheKey, lastOperation);
                } catch (IllegalArgumentException e) {
                    log.error("Invalid operation type in database: {}", operationTypes.get(0));
                }
            }
        }
        
        log.info("Validating turnstile passage - Personnel: {}, Turnstile: {}, RequestedOp: {}, LastOp: {}", 
                personelId, turnstileId, operationType, lastOperation);
        
        if (operationType == OperationType.OUT) {
            // for exit, the personel must have entered the turnstile before
            if (lastOperation == null || lastOperation != OperationType.IN) {
                throw new ValidationException(MessageType.TURNSTILE_EXIT_REQUIRES_PRIOR_ENTRY);
            }
        } else if (operationType == OperationType.IN) {
            // for entry, the personel must have exited the turnstile before or never entered the turnstile
            if (lastOperation != null && lastOperation == OperationType.IN) {
                throw new ValidationException(MessageType.TURNSTILE_ENTRY_REQUIRES_PRIOR_EXIT);
            }
        }
        
        // if the operation is valid, update the cache even if it is not saved to the database
        // this ensures consistency for concurrent requests
        latestOperationCache.put(cacheKey, operationType);
    }
}
