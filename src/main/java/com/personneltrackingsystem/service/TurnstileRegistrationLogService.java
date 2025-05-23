package com.personneltrackingsystem.service;

import com.personneltrackingsystem.dto.DtoTurnstileRegistrationLogIU;
import com.personneltrackingsystem.entity.OperationType;

public interface TurnstileRegistrationLogService {

    void saveOneTurnstileRegistrationLog(DtoTurnstileRegistrationLogIU dtoTurnstileRegistrationLogIU);

    boolean ifPersonelPassedTurnstile(Long personelId, Long turnstileId);
    
    OperationType getNextOperationType(Long personelId, Long turnstileId);

}
