package com.personneltrackingsystem.controller.impl;

import com.personneltrackingsystem.controller.TurnstileController;
import com.personneltrackingsystem.dto.DtoTurnstile;
import com.personneltrackingsystem.dto.DtoTurnstileIU;
import com.personneltrackingsystem.dto.DtoTurnstileBasedPersonnelEntry;
import com.personneltrackingsystem.dto.DtoTurnstilePassageFullRequest;
import com.personneltrackingsystem.dto.event.TurnstileRequestEvent;
import com.personneltrackingsystem.service.kafka.KafkaProducerService;
import com.personneltrackingsystem.service.TurnstileService;
import com.personneltrackingsystem.service.TurnstileRegistrationLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TurnstileControllerImpl implements TurnstileController {

    private final TurnstileService turnstileService;

    private final TurnstileRegistrationLogService turnstileRegistrationLogService;
    
    private final KafkaProducerService kafkaProducerService;
    

    @Override
    public List<DtoTurnstile> getAllTurnstiles() {
        return turnstileService.getAllTurnstiles();
    }

    @Override
    public DtoTurnstile getOneTurnstile(Long turnstileId) {
        return turnstileService.getOneTurnstile(turnstileId);
    }

    @Override
    public DtoTurnstile createTurnstile(DtoTurnstileIU newTurnstile) {
        return turnstileService.saveOneTurnstile(newTurnstile);
    }

    @Override
    public DtoTurnstile updateTurnstile(Long turnstileId, DtoTurnstileIU newTurnstile) {
        return turnstileService.updateOneTurnstile(turnstileId, newTurnstile);
    }

    @Override
    public void deleteTurnstile(Long turnstileId) {
        turnstileService.deleteOneTurnstile(turnstileId);
    }

    @Override
    @Transactional
    public ResponseEntity<String> passTurnstile(DtoTurnstilePassageFullRequest request) {
        log.info("Turnstile passage request received: {}", request);
        
        // validation and update the cache
        turnstileRegistrationLogService.validateTurnstilePassage(request);
        
        // convert DTO to Event
        TurnstileRequestEvent event = new TurnstileRequestEvent(
            request.getWantedToEnterTurnstileId(),
            request.getPersonelId(),
            request.getOperationType(),
            request.getOperationTimeStr()
        );
        
        // send event to Kafka
        kafkaProducerService.sendTurnstileRequestEvent(event);
        
        return ResponseEntity.accepted().body("Turnstile passage request accepted and is being processed");
    }
    
    
    @Override
    public HashMap<String, Map<String, List<DtoTurnstileBasedPersonnelEntry>>> getTurnstileBasedMonthlyPersonnelListWithHazelcast(YearMonth yearMonth) {
        
        // use service's validation method
        YearMonth targetMonth = turnstileRegistrationLogService.validateAndGetYearMonth(yearMonth);
        
        HashMap<String, Map<String, List<DtoTurnstileBasedPersonnelEntry>>> result = 
            turnstileRegistrationLogService.getMonthlyTurnstileBasedPersonnelListFromHazelcast(targetMonth);
            
        HashMap<String, Map<String, List<DtoTurnstileBasedPersonnelEntry>>> sortedResult = new LinkedHashMap<>();
        
        List<String> sortedDates = new ArrayList<>(result.keySet());
        sortedDates.sort(Comparator.reverseOrder());
        
        for (String date : sortedDates) {
            sortedResult.put(date, result.get(date));
        }
        
        return sortedResult;
    }
    
    @Override
    public HashMap<String, Map<String, List<DtoTurnstileBasedPersonnelEntry>>> getTurnstileBasedMonthlyPersonnelListWithRedis(YearMonth yearMonth) {
        
        // use service's validation method
        YearMonth targetMonth = turnstileRegistrationLogService.validateAndGetYearMonth(yearMonth);
        
        HashMap<String, Map<String, List<DtoTurnstileBasedPersonnelEntry>>> result = 
            turnstileRegistrationLogService.getMonthlyTurnstileBasedPersonnelListFromRedis(targetMonth);
            
        HashMap<String, Map<String, List<DtoTurnstileBasedPersonnelEntry>>> sortedResult = new LinkedHashMap<>();
        
        List<String> sortedDates = new ArrayList<>(result.keySet());
        sortedDates.sort(Comparator.reverseOrder());
        
        for (String date : sortedDates) {
            sortedResult.put(date, result.get(date));
        }
        
        return sortedResult;
    }

    @Override
    public Map<String, List<DtoTurnstileBasedPersonnelEntry>> getDailyTurnstilePassageRecords() {
        return turnstileService.getDailyTurnstilePassageRecords();
    }
} 