package com.personneltrackingsystem.service.impl;

import com.personneltrackingsystem.dto.DtoTurnstile;
import com.personneltrackingsystem.dto.DtoTurnstileIU;
import com.personneltrackingsystem.dto.DtoTurnstileRegistrationLogIU;
import com.personneltrackingsystem.dto.DtoTurnstilePassageFullRequest;
import com.personneltrackingsystem.entity.Gate;
import com.personneltrackingsystem.entity.OperationType;
import com.personneltrackingsystem.entity.Personel;
import com.personneltrackingsystem.entity.Turnstile;
import com.personneltrackingsystem.entity.Unit;
import com.personneltrackingsystem.event.TurnstilePassageEvent;
import com.personneltrackingsystem.exception.BaseException;
import com.personneltrackingsystem.exception.ErrorMessage;
import com.personneltrackingsystem.exception.MessageType;
import com.personneltrackingsystem.exception.ValidationException;
import com.personneltrackingsystem.mapper.TurnstileMapper;
import com.personneltrackingsystem.repository.TurnstileRepository;
import com.personneltrackingsystem.service.GateService;
import com.personneltrackingsystem.service.KafkaProducerService;
import com.personneltrackingsystem.service.PersonelService;
import com.personneltrackingsystem.service.TurnstileRegistrationLogService;
import com.personneltrackingsystem.service.TurnstileService;
import com.personneltrackingsystem.service.HazelcastCacheService;
import com.personneltrackingsystem.service.RedisCacheService;

import lombok.RequiredArgsConstructor;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class TurnstileServiceImpl implements TurnstileService {

    private final TurnstileRepository turnstileRepository;

    private final TurnstileRegistrationLogService turnstileRegistrationLogService;

    private final GateService gateService;

    private final PersonelService personelService;

    private final TurnstileMapper turnstileMapper;
    
    private final KafkaProducerService kafkaProducerService;

    private final HazelcastCacheService hazelcastCacheService;

    private final RedisCacheService redisCacheService;

    private static final Logger log = LoggerFactory.getLogger(TurnstileServiceImpl.class);

    @Override
    public List<DtoTurnstile> getAllTurnstiles(){

        List<Turnstile> turnstileList =  turnstileRepository.findAll();

        return turnstileMapper.turnstileListToDtoTurnstileList(turnstileList);
    }


    @Override
    public Optional<DtoTurnstile> getTurnstileById(Long turnstileId) {

        Turnstile turnstile = turnstileRepository.findById(turnstileId)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.TURNSTILE_NOT_FOUND, turnstileId.toString())));

        return Optional.ofNullable(turnstileMapper.turnstileToDtoTurnstile(turnstile));
    }


    @Override
    public DtoTurnstile getOneTurnstile(Long turnstileId){
        Optional<Turnstile> optTurnstile =  turnstileRepository.findById(turnstileId);
        if(optTurnstile.isEmpty()){
            throw new BaseException(new ErrorMessage(MessageType.TURNSTILE_NOT_FOUND, turnstileId.toString()));
        }else{
            return turnstileMapper.turnstileToDtoTurnstile(optTurnstile.get());
        }
    }


    @Override
    @Transactional
    public DtoTurnstile saveOneTurnstile(DtoTurnstileIU turnstile) {

        String turnstileName = turnstile.getTurnstileName();
        if (ObjectUtils.isEmpty(turnstileName)) {
            throw new ValidationException(MessageType.TURNSTILE_NAME_REQUIRED);
        }

        if (turnstileRepository.existsByTurnstileName(turnstileName)) {
            throw new ValidationException(MessageType.TURNSTILE_NAME_ALREADY_EXISTS, turnstileName);
        }

        // Find and set gate if gateId is provided
        if (ObjectUtils.isNotEmpty(turnstile.getGateId())) {
            Gate gate = gateService.checkIfGateExists(turnstile.getGateId());
            turnstile.setGateId(gate.getGateId());
        }

        Turnstile pTurnstile = turnstileMapper.dtoTurnstileIUToTurnstile(turnstile);
        Turnstile dbTurnstile = turnstileRepository.save(pTurnstile);

        return turnstileMapper.turnstileToDtoTurnstile(dbTurnstile);

    }


    @Override
    @Transactional
    public DtoTurnstile updateOneTurnstile(Long id, DtoTurnstileIU newTurnstile) {
        Turnstile existingTurnstile = turnstileRepository.findById(id)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.TURNSTILE_NOT_FOUND, id.toString())));

        if (ObjectUtils.isNotEmpty(newTurnstile.getTurnstileName())) {
            // Check uniqueness if the name is being changed
            if (!existingTurnstile.getTurnstileName().equals(newTurnstile.getTurnstileName()) && 
                turnstileRepository.existsByTurnstileName(newTurnstile.getTurnstileName())) {
                throw new ValidationException(MessageType.TURNSTILE_NAME_ALREADY_EXISTS, newTurnstile.getTurnstileName());
            }
            existingTurnstile.setTurnstileName(newTurnstile.getTurnstileName());
        }
        
        if (ObjectUtils.isNotEmpty(newTurnstile.getGateId())) {
            Gate gate = gateService.checkIfGateExists(newTurnstile.getGateId());
            existingTurnstile.setGateId(gate);
        }

        Turnstile updatedTurnstile = turnstileRepository.save(existingTurnstile);
        return turnstileMapper.turnstileToDtoTurnstile(updatedTurnstile);
    }


    @Override
    @Transactional
    public void deleteOneTurnstile(Long turnstileId) {
        Optional<Turnstile> optTurnstile = turnstileRepository.findById(turnstileId);

        if(optTurnstile.isPresent()){
            turnstileRepository.delete(optTurnstile.get());
        }
        else{
            throw new BaseException(new ErrorMessage(MessageType.TURNSTILE_NOT_FOUND, turnstileId.toString()));
        }
    }



    @Override
    public ResponseEntity<String> passTurnstile(DtoTurnstilePassageFullRequest request) {
        
        Long wantedToEnterTurnstileId = request.getWantedToEnterTurnstileId();
        String operationTimeStr = request.getOperationTimeStr();
        
        Turnstile turnstile = turnstileRepository.findById(wantedToEnterTurnstileId)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.TURNSTILE_NOT_FOUND, wantedToEnterTurnstileId.toString())));

        // get personel from cache (if not cached, it will be cached automatically)
        Personel personel = personelService.getPersonelWithCache(request.getPersonelId());

        // Parse the operation time from the input string
        LocalDateTime operationTime;
        if (operationTimeStr != null && !operationTimeStr.isEmpty()) {
            try {
                operationTime = LocalDateTime.parse(operationTimeStr, 
                    java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            } catch (Exception e) {
                throw new ValidationException(MessageType.INVALID_DATE_FORMAT);
            }
        } else {
            // Fallback to current time if no time string is provided
            operationTime = LocalDateTime.now();
        }

        DtoTurnstileRegistrationLogIU dtoTurnstileRegistrationLogIU = new DtoTurnstileRegistrationLogIU();

        dtoTurnstileRegistrationLogIU.setPersonelId((Long)personel.getPersonelId());
        dtoTurnstileRegistrationLogIU.setTurnstileId((Long)turnstile.getTurnstileId());
        dtoTurnstileRegistrationLogIU.setOperationTime(operationTime);
        
        // Use the operation type directly from the request
        dtoTurnstileRegistrationLogIU.setOperationType(request.getOperationType());

        turnstileRegistrationLogService.saveOneTurnstileRegistrationLog(dtoTurnstileRegistrationLogIU);

        // Clear cache at all passes because reporting is now done for all turnstiles
        YearMonth currentMonth = YearMonth.from(operationTime);
            
        // Always invalidate turnstile-based cache for all turnstiles
        hazelcastCacheService.removeTurnstileBasedMonthlyPersonnelListFromCache(currentMonth);
        redisCacheService.removeTurnstileBasedMonthlyPersonnelListFromCache(currentMonth);
        log.info("Turnstile-based cache invalidated for turnstile passage: {}", turnstile.getTurnstileName());

        // Only clear the personnel cache when passing through the main gate turnstiles
        Gate gate = turnstile.getGateId();
        if (gate != null && Boolean.TRUE.equals(gate.getMainEntrance())) {
            // Clear traditional cache for main entrance turnstiles only
            hazelcastCacheService.removeMonthlyPersonnelListFromCache(currentMonth);
            redisCacheService.removeMonthlyPersonnelListFromCache(currentMonth);
            log.info("Personnel-based cache invalidated for main entrance turnstile passage: {}", turnstile.getTurnstileName());
        }

        TurnstilePassageEvent event = new TurnstilePassageEvent(
            personel.getPersonelId(),
            personel.getName(),
            personel.getEmail(),
            turnstile.getTurnstileId(),
            turnstile.getTurnstileName(),
            operationTime,
            request.getOperationType()
        );
        
        // only send late arrival notifications under specific conditions:
        // 1. The operation must be an entry (IN)
        // 2. The turnstile must be at a main entrance
        // 3. The time must be after 9:15 AM (more than 15 minutes late)
        if (request.getOperationType() == OperationType.IN && gate != null && Boolean.TRUE.equals(gate.getMainEntrance())) {
            
            LocalTime entryTime = operationTime.toLocalTime();
            LocalTime lateThreshold = LocalTime.of(9, 15); 
            
            if (entryTime.isAfter(lateThreshold)) {
                Unit unit = gate.getUnitId();
                if (unit != null && unit.getAdministratorPersonelId() != null) {
         
                    Personel adminPersonel = unit.getAdministratorPersonelId();
                    
                    // update event with admin's email and mark as late notification
                    event.setRecipientEmail(adminPersonel.getEmail());
                    event.setRecipientName(adminPersonel.getName());
                    event.setIsAdminNotification(true);
                    event.setIsLateArrival(true);
                    
                    // calculate minutes late
                    long minutesLate = java.time.Duration.between(LocalTime.of(9, 0), entryTime).toMinutes();
                    event.setMinutesLate(minutesLate);
                    
                    kafkaProducerService.sendTurnstilePassageEvent(event);
                }
            }
        }
        
        return ResponseEntity.ok("Turnstile passed successfully");
    }
} 