package com.personneltrackingsystem.service.Impl;

import com.personneltrackingsystem.dto.DtoTurnstile;
import com.personneltrackingsystem.dto.DtoTurnstileIU;
import com.personneltrackingsystem.dto.DtoTurnstileRegistrationLogIU;
import com.personneltrackingsystem.entity.Gate;
import com.personneltrackingsystem.entity.Personel;
import com.personneltrackingsystem.entity.Turnstile;
import com.personneltrackingsystem.event.TurnstilePassageEvent;
import com.personneltrackingsystem.exception.BaseException;
import com.personneltrackingsystem.exception.ErrorMessage;
import com.personneltrackingsystem.exception.MessageResolver;
import com.personneltrackingsystem.exception.MessageType;
import com.personneltrackingsystem.exception.ValidationException;
import com.personneltrackingsystem.mapper.TurnstileMapper;
import com.personneltrackingsystem.repository.TurnstileRepository;
import com.personneltrackingsystem.service.GateService;
import com.personneltrackingsystem.service.KafkaProducerService;
import com.personneltrackingsystem.service.PersonelService;
import com.personneltrackingsystem.service.TurnstileRegistrationLogService;
import com.personneltrackingsystem.service.TurnstileService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TurnstileServiceImpl implements TurnstileService {

    private final TurnstileRepository turnstileRepository;

    private final TurnstileRegistrationLogService turnstileRegistrationLogService;

    private final GateService gateService;

    private final PersonelService personelService;

    private final TurnstileMapper turnstileMapper;

    private final MessageResolver messageResolver;
    
    private final KafkaProducerService kafkaProducerService;

    @Override
    public List<DtoTurnstile> getAllTurnstiles(){

        List<Turnstile> turnstileList =  turnstileRepository.findAll();

        return turnstileMapper.turnstileListToDtoTurnstileList(turnstileList);
    }

    @Override
    public Optional<DtoTurnstile> getTurnstileById(Long turnstileId) {

        Turnstile turnstile = turnstileRepository.findById(turnstileId)
                .orElseThrow(() -> new EntityNotFoundException("Turnstile not found with id: " + turnstileId));

        return Optional.ofNullable(turnstileMapper.turnstileToDtoTurnstile(turnstile));
    }

    @Override
    public DtoTurnstile getOneTurnstile(Long turnstileId){
        Optional<Turnstile> optTurnstile =  turnstileRepository.findById(turnstileId);
        if(optTurnstile.isEmpty()){
            ErrorMessage errorMessage = new ErrorMessage(MessageType.NO_RECORD_EXIST, messageResolver.toString());
            throw new BaseException(errorMessage);
        }else{
            return turnstileMapper.turnstileToDtoTurnstile(optTurnstile.get());
        }
    }

    @Override
    @Transactional
    public DtoTurnstile saveOneTurnstile(DtoTurnstileIU turnstile) {

        String turnstileName = turnstile.getTurnstileName();
        if (ObjectUtils.isEmpty(turnstileName)) {
            throw new BaseException(new ErrorMessage(MessageType.REQUIRED_FIELD_AVAILABLE, null));
        }

        if (turnstileRepository.existsByTurnstileName(turnstileName)) {
            throw new ValidationException("Turnstile with this turnstile name already exists!");
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
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, messageResolver.toString())));

        if (ObjectUtils.isNotEmpty(newTurnstile.getTurnstileName())) {
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
            ErrorMessage errorMessage = new ErrorMessage(MessageType.NO_RECORD_EXIST, messageResolver.toString());
            throw new BaseException(errorMessage);
        }
    }


    @Override
    public ResponseEntity<String> passTurnstile(Long turnstileId, Long personelId){
        Turnstile turnstile = turnstileRepository.findById(turnstileId)
                .orElseThrow(() -> new EntityNotFoundException("Turnstile not found with id: " + turnstileId));

        Personel personel = personelService.checkIfPersonelExists(personelId);

        // Use the same timestamp for both database record and Kafka event
        LocalDateTime operationTime = LocalDateTime.now();

        DtoTurnstileRegistrationLogIU dtoTurnstileRegistrationLogIU = new DtoTurnstileRegistrationLogIU();

        dtoTurnstileRegistrationLogIU.setPersonelId((Long)personel.getPersonelId());
        dtoTurnstileRegistrationLogIU.setTurnstileId((Long)turnstile.getTurnstileId());
        dtoTurnstileRegistrationLogIU.setOperationTime(operationTime);

        // Get the next operation type based on the last operation for this personnel and turnstile
        String operationType = turnstileRegistrationLogService.getNextOperationType(personel.getPersonelId(), turnstile.getTurnstileId());
        dtoTurnstileRegistrationLogIU.setOperationType(operationType);

        turnstileRegistrationLogService.saveOneTurnstileRegistrationLog(dtoTurnstileRegistrationLogIU);

        // Publish turnstile passage event to Kafka
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