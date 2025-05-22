package com.personneltrackingsystem.service.Impl;

import org.springframework.stereotype.Service;

import com.personneltrackingsystem.dto.DtoTurnstileRegistrationLogIU;
import com.personneltrackingsystem.entity.TurnstileRegistrationLog;
import com.personneltrackingsystem.mapper.TurnstileRegistrationLogMapper;
import com.personneltrackingsystem.repository.TurnstileRegistrationLogRepository;
import com.personneltrackingsystem.service.TurnstileRegistrationLogService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TurnstileRegistrationLogServiceImpl implements TurnstileRegistrationLogService {

    private final TurnstileRegistrationLogRepository turnstileRegistrationLogRepository;

    private final TurnstileRegistrationLogMapper turnstileRegistrationLogMapper;

    
    @Override
    public void saveOneTurnstileRegistrationLog(DtoTurnstileRegistrationLogIU dtoTurnstileRegistrationLogIU){
        TurnstileRegistrationLog turnstileRegistrationLog = turnstileRegistrationLogMapper.dtoTurnstileRegistrationLogIUToTurnstileRegistrationLog(dtoTurnstileRegistrationLogIU);
        turnstileRegistrationLogRepository.save(turnstileRegistrationLog);
    }

    @Override
    public boolean ifPersonelPassedTurnstile(Long personelId, Long turnstileId){
        return turnstileRegistrationLogRepository.passedTurnstile(personelId, turnstileId);
    }

    @Override
    public String getNextOperationType(Long personelId, Long turnstileId) {
        java.util.List<String> operationTypes = turnstileRegistrationLogRepository.findOperationTypesByPersonelAndTurnstile(personelId, turnstileId);
        
        // If there are no previous records or the last operation was OUT, the next should be IN
        if (operationTypes == null || operationTypes.isEmpty() || "OUT".equals(operationTypes.get(0))) {
            return "IN";
        } else {
            // If the last operation was IN, the next should be OUT
            return "OUT";
        }
    }

}
