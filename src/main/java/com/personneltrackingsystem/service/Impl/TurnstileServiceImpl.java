package com.personneltrackingsystem.service.Impl;

import com.personneltrackingsystem.dto.DtoTurnstile;
import com.personneltrackingsystem.dto.DtoTurnstileIU;
import com.personneltrackingsystem.dto.DtoTurnstileRegistrationLogIU;
import com.personneltrackingsystem.entity.Gate;
import com.personneltrackingsystem.entity.OperationType;
import com.personneltrackingsystem.entity.Personel;
import com.personneltrackingsystem.entity.Turnstile;
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

    private final HazelcastCacheService hazelcastCacheService;

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
    public ResponseEntity<String> passTurnstile(Long turnstileId, Long personelId){
        Turnstile turnstile = turnstileRepository.findById(turnstileId)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.TURNSTILE_NOT_FOUND, turnstileId.toString())));

        // get personel from cache (if not cached, it will be cached automatically)
        Personel personel = personelService.getPersonelWithCache(personelId);

        // same timestamp for both database record and Kafka event
        LocalDateTime operationTime = LocalDateTime.now();

        DtoTurnstileRegistrationLogIU dtoTurnstileRegistrationLogIU = new DtoTurnstileRegistrationLogIU();

        dtoTurnstileRegistrationLogIU.setPersonelId((Long)personel.getPersonelId());
        dtoTurnstileRegistrationLogIU.setTurnstileId((Long)turnstile.getTurnstileId());
        dtoTurnstileRegistrationLogIU.setOperationTime(operationTime);

        // next operation type based on the last operation for this personnel and turnstile
        OperationType operationType = turnstileRegistrationLogService.getNextOperationType(personel.getPersonelId(), turnstile.getTurnstileId());
        dtoTurnstileRegistrationLogIU.setOperationType(operationType);

        turnstileRegistrationLogService.saveOneTurnstileRegistrationLog(dtoTurnstileRegistrationLogIU);

        // invalidate today's daily personnel cache since new entry is added
        hazelcastCacheService.removeDailyPersonnelListFromCache(LocalDate.now());
        redisCacheService.removeDailyPersonnelListFromCache(LocalDate.now());

        // publish turnstile passage event to kafka
        TurnstilePassageEvent event = new TurnstilePassageEvent(
            personel.getPersonelId(),
            personel.getName(),
            personel.getEmail(),
            turnstile.getTurnstileId(),
            turnstile.getTurnstileName(),
            operationTime,
            operationType
        );
        
        kafkaProducerService.sendTurnstilePassageEvent(event);

        return ResponseEntity.ok("Turnstile passed successfully");
    }
} 