package com.personneltrackingsystem.service;

import com.personneltrackingsystem.dto.DtoTurnstileRegistrationLogIU;
import com.personneltrackingsystem.dto.DtoDailyPersonnelEntry;
import com.personneltrackingsystem.entity.OperationType;
import com.personneltrackingsystem.entity.Personel;
import com.personneltrackingsystem.entity.TurnstileRegistrationLog;

import java.time.LocalDate;
import java.util.List;

public interface TurnstileRegistrationLogService {

    void saveOneTurnstileRegistrationLog(DtoTurnstileRegistrationLogIU dtoTurnstileRegistrationLogIU);

    boolean ifPersonelPassedTurnstile(Long personelId, Long turnstileId);
    
    OperationType getNextOperationType(Long personelId, Long turnstileId);

    List<DtoDailyPersonnelEntry> getDailyPersonnelList(LocalDate date);

    List<DtoDailyPersonnelEntry> getDailyPersonnelListWithRedis(LocalDate date);

    List<DtoDailyPersonnelEntry> getDailyPersonnelListFromDatabase(LocalDate date);

    DtoDailyPersonnelEntry createDailyPersonnelEntry(Personel personel, List<TurnstileRegistrationLog> logs);

}
