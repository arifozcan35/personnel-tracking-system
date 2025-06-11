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
import com.personneltrackingsystem.service.cache.HazelcastCacheService;
import com.personneltrackingsystem.service.cache.RedisCacheService;

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

import org.apache.commons.lang3.ObjectUtils;
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
    

    private final ConcurrentHashMap<String, OperationType> latestOperationCache = new ConcurrentHashMap<>();


    @Scheduled(cron = "0 0 0 * * *")
    public void dailyCacheRefresh() {
        log.info("Daily cache refresh process started - midnight");
        
        try {
            Map<String, Map<String, List<DtoTurnstileBasedPersonnelEntry>>> allDailyRecords = 
                redisCacheService.getAllDailyTurnstilePassageRecords();

            if (ObjectUtils.isNotEmpty(allDailyRecords) && !allDailyRecords.isEmpty()) {
                for (Map.Entry<String, Map<String, List<DtoTurnstileBasedPersonnelEntry>>> dateEntry : allDailyRecords.entrySet()) {
                    String dateStr = dateEntry.getKey();
                    Map<String, List<DtoTurnstileBasedPersonnelEntry>> dailyRecords = dateEntry.getValue();

                    LocalDate recordDate = LocalDate.parse(dateStr, 
                        java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd"));

                    hazelcastCacheService.addDailyRecordsToMonthlyMap(recordDate, dailyRecords);
                    
                    log.info("Transferred daily turnstile passage records for date: {} to Hazelcast monthly map", dateStr);
                }
            }

            redisCacheService.transferDailyRecordsToMonthlyMap();

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
        
        log.info("Saved turnstile registration log: Personel {} - Turnstile {} - Operation {}", 
                dtoTurnstileRegistrationLogIU.getPersonelId(), 
                dtoTurnstileRegistrationLogIU.getTurnstileId(),
                dtoTurnstileRegistrationLogIU.getOperationType());
    }

    
    @Override
    public HashMap<String, Map<String, List<DtoTurnstileBasedPersonnelEntry>>> getMonthlyTurnstileBasedPersonnelListFromHazelcast(YearMonth yearMonth) {
        Optional<HashMap<String, Map<String, List<DtoTurnstileBasedPersonnelEntry>>>> cachedResult = 
            hazelcastCacheService.getTurnstileBasedMonthlyPersonnelListFromCache(yearMonth);
            
        if (cachedResult.isPresent()) {
            log.info("Turnstile-based monthly personnel list retrieved from Hazelcast cache: {}", yearMonth);
            return cachedResult.get();
        }

        log.info("Turnstile-based monthly personnel list not found in Hazelcast for month: {}", yearMonth);
        return new HashMap<>();
    }
    
    @Override
    public HashMap<String, Map<String, List<DtoTurnstileBasedPersonnelEntry>>> getMonthlyTurnstileBasedPersonnelListFromRedis(YearMonth yearMonth) {
        Optional<HashMap<String, Map<String, List<DtoTurnstileBasedPersonnelEntry>>>> cachedResult = 
            redisCacheService.getTurnstileBasedMonthlyPersonnelListFromCache(yearMonth);
        
        if (cachedResult.isPresent()) {
            log.info("Turnstile-based monthly personnel list retrieved from Redis: {}", yearMonth);
            return cachedResult.get();
        }

        log.info("Turnstile-based monthly personnel list not found in Redis for month: {}", yearMonth);
        return new HashMap<>();
    }
    
    
    @Override
    public YearMonth validateAndGetYearMonth(YearMonth yearMonth) {
        if (ObjectUtils.isNotEmpty(yearMonth)) {
            try {
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

        OperationType lastOperation = null;
        List<String> operationTypes = turnstileRegistrationLogRepository.findOperationTypesByPersonelAndTurnstile(personelId, turnstileId);
        
        if (!operationTypes.isEmpty()) {
            try {
                lastOperation = OperationType.valueOf(operationTypes.get(0));
                log.info("Last operation found: {} (Personel: {}, Turnstile: {})", lastOperation, personelId, turnstileId);
            } catch (IllegalArgumentException e) {
                log.error("Invalid operation type in database: {}", operationTypes.get(0));
            }
        } else {
            log.info("No previous operation found for personel {} on turnstile {}", personelId, turnstileId);
        }
        
        log.info("Turnstile passage validation - Personel: {}, Turnstile: {}, Requested Operation: {}, Last Operation: {}", 
                personelId, turnstileId, operationType, lastOperation);
        

        if (ObjectUtils.isEmpty(lastOperation)) {
            if (operationType != OperationType.IN) {
                log.warn("First operation must be IN but received: {} (Personel: {}, Turnstile: {})", 
                        operationType, personelId, turnstileId);
                throw new ValidationException(MessageType.TURNSTILE_EXIT_REQUIRES_PRIOR_ENTRY);
            }
        } else if (lastOperation == OperationType.IN) {
            if (operationType != OperationType.OUT) {
                log.warn("Only OUT operation is allowed when the last operation is IN (Personel: {}, Turnstile: {})", 
                        personelId, turnstileId);
                throw new ValidationException(MessageType.TURNSTILE_ENTRY_REQUIRES_PRIOR_EXIT);
            }
        } else if (lastOperation == OperationType.OUT) {
            if (operationType != OperationType.IN) {
                log.warn("Only IN operation is allowed when the last operation is OUT (Personel: {}, Turnstile: {})", 
                        personelId, turnstileId);
                throw new ValidationException(MessageType.TURNSTILE_EXIT_REQUIRES_PRIOR_ENTRY);
            }
        }
        
        log.info("Operation validated successfully: {} (Personel: {}, Turnstile: {})", 
                operationType, personelId, turnstileId);
    }
}
