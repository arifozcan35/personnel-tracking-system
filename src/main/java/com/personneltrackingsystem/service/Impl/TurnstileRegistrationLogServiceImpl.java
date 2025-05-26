package com.personneltrackingsystem.service.Impl;

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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

    
    @Override
    public void saveOneTurnstileRegistrationLog(DtoTurnstileRegistrationLogIU dtoTurnstileRegistrationLogIU){
        TurnstileRegistrationLog turnstileRegistrationLog = turnstileRegistrationLogMapper.dtoTurnstileRegistrationLogIUToTurnstileRegistrationLog(dtoTurnstileRegistrationLogIU);
        turnstileRegistrationLogRepository.save(turnstileRegistrationLog);

        // Invalidate today's cache when new log is added
        hazelcastCacheService.removeDailyPersonnelListFromCache(LocalDate.now());
    }

    @Override
    public boolean ifPersonelPassedTurnstile(Long personelId, Long turnstileId){
        return turnstileRegistrationLogRepository.passedTurnstile(personelId, turnstileId);
    }

    @Override
    public OperationType getNextOperationType(Long personelId, Long turnstileId) {
        java.util.List<String> operationTypes = turnstileRegistrationLogRepository.findOperationTypesByPersonelAndTurnstile(personelId, turnstileId);
        
        // If there are no previous records or the last operation was OUT, the next should be IN
        if (operationTypes == null || operationTypes.isEmpty() || OperationType.OUT.getValue().equals(operationTypes.get(0))) {
            return OperationType.IN;
        } else {
            // If the last operation was IN, the next should be OUT
            return OperationType.OUT;
        }
    }

    @Override
    public List<DtoDailyPersonnelEntry> getDailyPersonnelList(LocalDate date) {
        // First try to get from Hazelcast cache
        Optional<List<DtoDailyPersonnelEntry>> cachedResult = hazelcastCacheService.getDailyPersonnelListFromCache(date);
        if (cachedResult.isPresent()) {
            log.info("Returning daily personnel list from Hazelcast cache for date: {}", date);
            return cachedResult.get();
        }

        // If not in cache, get from database
        log.info("Daily personnel list not found in cache, fetching from database for date: {}", date);
        List<DtoDailyPersonnelEntry> dailyPersonnelList = getDailyPersonnelListFromDatabase(date);

        // Cache the result in Hazelcast
        hazelcastCacheService.cacheDailyPersonnelList(date, dailyPersonnelList);

        return dailyPersonnelList;
    }

    private List<DtoDailyPersonnelEntry> getDailyPersonnelListFromDatabase(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        
        // Get all turnstile logs for the given date
        List<TurnstileRegistrationLog> allLogs = turnstileRegistrationLogRepository.findAllByDate(startOfDay);
        
        if (allLogs.isEmpty()) {
            return new ArrayList<>();
        }

        // Group logs by personnel
        Map<Long, List<TurnstileRegistrationLog>> logsByPersonnel = allLogs.stream()
                .collect(Collectors.groupingBy(log -> log.getPersonelId().getPersonelId()));

        List<DtoDailyPersonnelEntry> dailyPersonnelList = new ArrayList<>();

        for (Map.Entry<Long, List<TurnstileRegistrationLog>> entry : logsByPersonnel.entrySet()) {
            Long personelId = entry.getKey();
            List<TurnstileRegistrationLog> personnelLogs = entry.getValue();

            // Get personnel details (using cache)
            Personel personel = personelService.getPersonelWithCache(personelId);

            // Create daily personnel entry
            DtoDailyPersonnelEntry dailyEntry = createDailyPersonnelEntry(personel, personnelLogs);
            dailyPersonnelList.add(dailyEntry);
        }

        return dailyPersonnelList;
    }

    private DtoDailyPersonnelEntry createDailyPersonnelEntry(Personel personel, List<TurnstileRegistrationLog> logs) {
        DtoDailyPersonnelEntry dailyEntry = new DtoDailyPersonnelEntry();
        
        dailyEntry.setPersonelId(personel.getPersonelId());
        dailyEntry.setPersonelName(personel.getName());
        dailyEntry.setPersonelEmail(personel.getEmail());

        // Sort logs by operation time
        logs.sort((log1, log2) -> log1.getOperationTime().compareTo(log2.getOperationTime()));

        // Set first entry time (first IN operation)
        logs.stream()
             .filter(log -> log.getOperationType() == OperationType.IN)
             .findFirst()
             .ifPresent(log -> dailyEntry.setFirstEntryTime(log.getOperationTime()));

        // Set last exit time (last OUT operation)
        logs.stream()
             .filter(log -> log.getOperationType() == OperationType.OUT)
             .reduce((first, second) -> second)
             .ifPresent(log -> dailyEntry.setLastExitTime(log.getOperationTime()));

        // Set current status (last operation type)
        if (!logs.isEmpty()) {
            TurnstileRegistrationLog lastLog = logs.get(logs.size() - 1);
            dailyEntry.setCurrentStatus(lastLog.getOperationType());
        }

        // Create passages list
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

}
