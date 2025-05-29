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
import com.personneltrackingsystem.service.RedisCacheService;

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

    private final RedisCacheService redisCacheService;

    
    @Override
    public void saveOneTurnstileRegistrationLog(DtoTurnstileRegistrationLogIU dtoTurnstileRegistrationLogIU){
        TurnstileRegistrationLog turnstileRegistrationLog = turnstileRegistrationLogMapper.dtoTurnstileRegistrationLogIUToTurnstileRegistrationLog(dtoTurnstileRegistrationLogIU);
        turnstileRegistrationLogRepository.save(turnstileRegistrationLog);

        // invalidate today's cache when new log is added
        hazelcastCacheService.removeDailyPersonnelListFromCache(LocalDate.now());
        redisCacheService.removeDailyPersonnelListFromCache(LocalDate.now());
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
    public List<DtoDailyPersonnelEntry> getDailyPersonnelList(LocalDate date) {
        // first try to get from hazelcast cache
        Optional<List<DtoDailyPersonnelEntry>> cachedResult = hazelcastCacheService.getDailyPersonnelListFromCache(date);
        if (cachedResult.isPresent()) {
            log.info("returning daily personnel list from hazelcast cache for date: {}", date);
            return cachedResult.get();
        }

        // if not in cache, get from database
        log.info("Daily personnel list not found in cache, fetching from database for date: {}", date);
        List<DtoDailyPersonnelEntry> dailyPersonnelList = getDailyPersonnelListFromDatabase(date);

        // cache the result in hazelcast
        hazelcastCacheService.cacheDailyPersonnelList(date, dailyPersonnelList);

        return dailyPersonnelList;
    }

    
    @Override
    public List<DtoDailyPersonnelEntry> getDailyPersonnelListWithRedis(LocalDate date) {
        // first try to get from redis cache
        Optional<List<DtoDailyPersonnelEntry>> cachedResult = redisCacheService.getDailyPersonnelListFromCache(date);
        if (cachedResult.isPresent()) {
            log.info("returning daily personnel list from redis cache for date: {}", date);
            return cachedResult.get();
        }

        // if not in cache, get from database
        log.info("Daily personnel list not found in redis cache, fetching from database for date: {}", date);
        List<DtoDailyPersonnelEntry> dailyPersonnelList = getDailyPersonnelListFromDatabase(date);

        // cache the result in redis
        redisCacheService.cacheDailyPersonnelList(date, dailyPersonnelList);

        return dailyPersonnelList;
    }


    @Override
    public List<DtoDailyPersonnelEntry> getDailyPersonnelListFromDatabase(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        
        // get all turnstile logs according to date
        List<TurnstileRegistrationLog> allLogs = turnstileRegistrationLogRepository.findAllByDate(startOfDay);
        
        if (allLogs.isEmpty()) {
            return new ArrayList<>();
        }

        Map<Long, List<TurnstileRegistrationLog>> logsByPersonnel = allLogs.stream()
                .collect(Collectors.groupingBy(log -> log.getPersonelId().getPersonelId()));

        List<DtoDailyPersonnelEntry> dailyPersonnelList = new ArrayList<>();

        for (Map.Entry<Long, List<TurnstileRegistrationLog>> entry : logsByPersonnel.entrySet()) {
            Long personelId = entry.getKey();
            List<TurnstileRegistrationLog> personnelLogs = entry.getValue();

            // get personel details with cache
            Personel personel = personelService.getPersonelWithCache(personelId);

            // create daily personel entry
            DtoDailyPersonnelEntry dailyEntry = createDailyPersonnelEntry(personel, personnelLogs);
            dailyPersonnelList.add(dailyEntry);
        }

        return dailyPersonnelList;
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

}
