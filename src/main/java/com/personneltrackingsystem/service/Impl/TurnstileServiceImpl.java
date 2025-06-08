package com.personneltrackingsystem.service.impl;

import com.personneltrackingsystem.dto.DtoTurnstile;
import com.personneltrackingsystem.dto.DtoTurnstileIU;
import com.personneltrackingsystem.dto.DtoTurnstileRegistrationLogIU;
import com.personneltrackingsystem.dto.DtoTurnstilePassageFullRequest;
import com.personneltrackingsystem.dto.DtoTurnstileBasedPersonnelEntry;
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
import com.personneltrackingsystem.service.RedisCacheService;
import com.personneltrackingsystem.service.TurnstileRegistrationLogService;
import com.personneltrackingsystem.service.TurnstileService;

import lombok.RequiredArgsConstructor;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.LocalDate;


@Service
@RequiredArgsConstructor
public class TurnstileServiceImpl implements TurnstileService {

    private final TurnstileRepository turnstileRepository;

    private final TurnstileRegistrationLogService turnstileRegistrationLogService;

    private final GateService gateService;

    private final PersonelService personelService;

    private final TurnstileMapper turnstileMapper;
    
    private final KafkaProducerService kafkaProducerService;
    
    private final RedisCacheService redisCacheService;


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

        // find and set gate if gateId is provided
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
            // check uniqueness if the name is being changed
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

        // parse the operation time from the input string
        LocalDateTime operationTime;
        if (operationTimeStr != null && !operationTimeStr.isEmpty()) {
            try {
                // user now provides only time portion (HH:mm:ss)
                LocalTime timeFromUser = LocalTime.parse(operationTimeStr, 
                    java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));
                
                // combine current date with user-provided time
                LocalDate currentDate = LocalDate.now();
                operationTime = LocalDateTime.of(currentDate, timeFromUser);
            } catch (Exception e) {
                throw new ValidationException(MessageType.INVALID_TIME_FORMAT);
            }
        } else {
            // fallback to current time if no time string is provided
            operationTime = LocalDateTime.now();
        }

        // create turnstile registration log for database
        DtoTurnstileRegistrationLogIU dtoTurnstileRegistrationLogIU = new DtoTurnstileRegistrationLogIU();
        dtoTurnstileRegistrationLogIU.setPersonelId(personel.getPersonelId());
        dtoTurnstileRegistrationLogIU.setTurnstileId(turnstile.getTurnstileId());
        dtoTurnstileRegistrationLogIU.setOperationTime(operationTime);
        dtoTurnstileRegistrationLogIU.setOperationType(request.getOperationType());

        // save to database
        turnstileRegistrationLogService.saveOneTurnstileRegistrationLog(dtoTurnstileRegistrationLogIU);
        
        // create personnel entry for Redis
        DtoTurnstileBasedPersonnelEntry redisTurnstileEntry = new DtoTurnstileBasedPersonnelEntry();
        redisTurnstileEntry.setPersonelId(personel.getPersonelId());
        redisTurnstileEntry.setPersonelName(personel.getName());
        redisTurnstileEntry.setPersonelEmail(personel.getEmail());
        redisTurnstileEntry.setOperationTime(operationTime);
        redisTurnstileEntry.setOperationType(request.getOperationType());
        
        // extract the date from operation time
        LocalDate recordDate = operationTime.toLocalDate();
        
        // save to Redis daily map with record date
        redisCacheService.addToDailyTurnstilePassageRecord(turnstile.getTurnstileName(), redisTurnstileEntry, recordDate);

        
        // get the gate for late arrival notification checks
        Gate gate = turnstile.getGateId();

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
        if (request.getOperationType() == OperationType.IN && ObjectUtils.isNotEmpty(gate) && Boolean.TRUE.equals(gate.getMainEntrance())) {
            
            LocalTime entryTime = operationTime.toLocalTime();
            LocalTime lateThreshold = LocalTime.of(9, 15); 
            
            if (entryTime.isAfter(lateThreshold)) {
                Unit unit = gate.getUnitId();
                if (unit != null && unit.getAdministratorPersonelId() != null) {
         
                    Personel adminPersonel = unit.getAdministratorPersonelId();
                    
                    // update event with admin's email and mark as late arrival
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

    @Override
    public Map<String, List<DtoTurnstileBasedPersonnelEntry>> getDailyTurnstilePassageRecords() {
        LocalDate today = LocalDate.now();
        return redisCacheService.getDailyTurnstilePassageRecordsByDate(today);
    }
} 