package com.personneltrackingsystem.service.impl;

import org.springframework.stereotype.Service;

import com.personneltrackingsystem.dto.DtoTurnstileRegistrationLogIU;
import com.personneltrackingsystem.dto.DtoDailyPersonnelEntry;
import com.personneltrackingsystem.dto.DtoDailyPersonnelEntry.DtoTurnstilePassage;
import com.personneltrackingsystem.entity.OperationType;
import com.personneltrackingsystem.entity.Personel;
import com.personneltrackingsystem.entity.TurnstileRegistrationLog;
import com.personneltrackingsystem.mapper.TurnstileRegistrationLogMapper;
import com.personneltrackingsystem.repository.TurnstileRegistrationLogRepository;
import com.personneltrackingsystem.service.TurnstileRegistrationLogService;
import com.personneltrackingsystem.service.PersonelService;
import com.personneltrackingsystem.service.HazelcastCacheService;
import com.personneltrackingsystem.service.RedisCacheService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TurnstileRegistrationLogServiceImpl implements TurnstileRegistrationLogService {

    private final TurnstileRegistrationLogRepository turnstileRegistrationLogRepository;

    private final TurnstileRegistrationLogMapper turnstileRegistrationLogMapper;

    private final PersonelService personelService;

    private final HazelcastCacheService hazelcastCacheService;

    private final RedisCacheService redisCacheService;

    
    @Override
    public void saveOneTurnstileRegistrationLog(DtoTurnstileRegistrationLogIU dtoTurnstileRegistrationLogIU){
        TurnstileRegistrationLog turnstileRegistrationLog = turnstileRegistrationLogMapper.dtoTurnstileRegistrationLogIUToTurnstileRegistrationLog(dtoTurnstileRegistrationLogIU);
        turnstileRegistrationLogRepository.save(turnstileRegistrationLog);
        
        // Cache invalidation işlemi TurnstileServiceImpl sınıfında ana kapı kontrolü yapılarak gerçekleştiriliyor
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


    @Override
    public DtoDailyPersonnelEntry createDailyPersonnelEntry(Personel personel, List<TurnstileRegistrationLog> logs) {
        DtoDailyPersonnelEntry dailyEntry = new DtoDailyPersonnelEntry();
        
        dailyEntry.setPersonelId(personel.getPersonelId());
        dailyEntry.setPersonelName(personel.getName());
        dailyEntry.setPersonelEmail(personel.getEmail());

        // sort logs by operation time
        logs.sort((log1, log2) -> log1.getOperationTime().compareTo(log2.getOperationTime()));

        // set first entry time (first IN operation)
        logs.stream()
             .filter(log -> log.getOperationType() == OperationType.IN)
             .findFirst()
             .ifPresent(log -> dailyEntry.setFirstEntryTime(log.getOperationTime()));

        // set last exit time (last OUT operation)
        logs.stream()
             .filter(log -> log.getOperationType() == OperationType.OUT)
             .reduce((first, second) -> second)
             .ifPresent(log -> dailyEntry.setLastExitTime(log.getOperationTime()));

        // set current status (last operation type)
        if (!logs.isEmpty()) {
            TurnstileRegistrationLog lastLog = logs.get(logs.size() - 1);
            dailyEntry.setCurrentStatus(lastLog.getOperationType());
        }

        // create passages list
        List<DtoTurnstilePassage> passages = logs.stream()
                .map(this::createTurnstilePassage)
                .collect(Collectors.toList());
        dailyEntry.setPassages(passages);

        return dailyEntry;
    }

    private DtoTurnstilePassage createTurnstilePassage(TurnstileRegistrationLog log) {
        DtoTurnstilePassage passage = new DtoTurnstilePassage();

        passage.setTurnstileId(log.getTurnstileId().getTurnstileId());
        passage.setTurnstileName(log.getTurnstileId().getTurnstileName());
        passage.setOperationTime(log.getOperationTime());
        passage.setOperationType(log.getOperationType());
        return passage;
    }
    
    // Monthly personnel list methods
    
    @Override
    public HashMap<String, List<DtoDailyPersonnelEntry>> getMonthlyMainEntrancePersonnelList(YearMonth yearMonth) {
        // First try to get from hazelcast cache
        Optional<HashMap<String, List<DtoDailyPersonnelEntry>>> cachedResult = 
            hazelcastCacheService.getMonthlyPersonnelListFromCache(yearMonth);
            
        if (cachedResult.isPresent()) {
            log.info("Returning monthly main entrance personnel list from cache for month: {}", yearMonth);
            return cachedResult.get();
        }
        
        // If not in cache, get from database
        log.info("Monthly main entrance personnel list not found in cache, fetching from database for month: {}", yearMonth);
        HashMap<String, List<DtoDailyPersonnelEntry>> monthlyData = getMonthlyMainEntrancePersonnelListFromDatabase(yearMonth);
        
        // Cache the result
        hazelcastCacheService.cacheMonthlyPersonnelList(yearMonth, monthlyData);
        
        return monthlyData;
    }
    
    @Override
    public HashMap<String, List<DtoDailyPersonnelEntry>> getMonthlyMainEntrancePersonnelListFromDatabase(YearMonth yearMonth) {
        // Get all turnstile logs for the specified month from main entrances
        List<TurnstileRegistrationLog> allMonthLogs = turnstileRegistrationLogRepository.findAllMainEntranceLogsByMonth(
            yearMonth.getYear(), yearMonth.getMonthValue());
        
        if (allMonthLogs.isEmpty()) {
            return new HashMap<>();
        }
        
        // Group logs by date
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Map<String, List<TurnstileRegistrationLog>> logsByDate = allMonthLogs.stream()
            .collect(Collectors.groupingBy(log -> log.getOperationTime().format(dateFormatter)));
        
        // Create result map
        HashMap<String, List<DtoDailyPersonnelEntry>> result = new HashMap<>();
        
        // Process each date
        for (Map.Entry<String, List<TurnstileRegistrationLog>> dateEntry : logsByDate.entrySet()) {
            String dateStr = dateEntry.getKey();
            List<TurnstileRegistrationLog> logsForDate = dateEntry.getValue();
            
            // Group logs by personnel for this date
            Map<Long, List<TurnstileRegistrationLog>> logsByPersonnel = logsForDate.stream()
                .collect(Collectors.groupingBy(log -> log.getPersonelId().getPersonelId()));
            
            List<DtoDailyPersonnelEntry> personnelEntries = new ArrayList<>();
            
            // Process each personnel's logs
            for (Map.Entry<Long, List<TurnstileRegistrationLog>> personnelEntry : logsByPersonnel.entrySet()) {
                Long personelId = personnelEntry.getKey();
                List<TurnstileRegistrationLog> personnelLogs = personnelEntry.getValue();
                
                // Get personnel details
                Personel personel = personelService.getPersonelWithCache(personelId);
                
                // Create entry for this personnel
                DtoDailyPersonnelEntry dailyEntry = createDailyPersonnelEntry(personel, personnelLogs);
                personnelEntries.add(dailyEntry);
            }
            
            // Add to result map
            result.put(dateStr, personnelEntries);
        }
        
        return result;
    }

    @Override
    public HashMap<String, List<DtoDailyPersonnelEntry>> getMonthlyMainEntrancePersonnelListWithRedis(YearMonth yearMonth) {
        // First try to get from redis cache
        Optional<HashMap<String, List<DtoDailyPersonnelEntry>>> cachedResult = 
            redisCacheService.getMonthlyPersonnelListFromCache(yearMonth);
            
        if (cachedResult.isPresent()) {
            log.info("Returning monthly main entrance personnel list from redis cache for month: {}", yearMonth);
            return cachedResult.get();
        }
        
        // If not in cache, get from database
        log.info("Monthly main entrance personnel list not found in redis cache, fetching from database for month: {}", yearMonth);
        HashMap<String, List<DtoDailyPersonnelEntry>> monthlyData = getMonthlyMainEntrancePersonnelListFromDatabase(yearMonth);
        
        // Cache the result
        redisCacheService.cacheMonthlyPersonnelList(yearMonth, monthlyData);
        
        return monthlyData;
    }
}
