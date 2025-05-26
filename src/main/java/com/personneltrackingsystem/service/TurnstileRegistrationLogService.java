package com.personneltrackingsystem.service;

import com.personneltrackingsystem.dto.DtoTurnstileRegistrationLogIU;
import com.personneltrackingsystem.dto.DtoDailyPersonnelEntry;
import com.personneltrackingsystem.entity.OperationType;

import java.time.LocalDate;
import java.util.List;

public interface TurnstileRegistrationLogService {

    void saveOneTurnstileRegistrationLog(DtoTurnstileRegistrationLogIU dtoTurnstileRegistrationLogIU);

    boolean ifPersonelPassedTurnstile(Long personelId, Long turnstileId);
    
    OperationType getNextOperationType(Long personelId, Long turnstileId);

    List<DtoDailyPersonnelEntry> getDailyPersonnelList(LocalDate date);

}
