package com.personneltrackingsystem.service;

import com.personneltrackingsystem.dto.DtoTurnstileRegistrationLogIU;
import com.personneltrackingsystem.dto.DtoDailyPersonnelEntry;
import com.personneltrackingsystem.entity.OperationType;
import com.personneltrackingsystem.entity.Personel;
import com.personneltrackingsystem.entity.TurnstileRegistrationLog;

import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;

public interface TurnstileRegistrationLogService {

    void saveOneTurnstileRegistrationLog(DtoTurnstileRegistrationLogIU dtoTurnstileRegistrationLogIU);

    boolean ifPersonelPassedTurnstile(Long personelId, Long turnstileId);
    
    OperationType getNextOperationType(Long personelId, Long turnstileId);

    // Yardımcı metod, aylık listeleme için de kullanılıyor
    DtoDailyPersonnelEntry createDailyPersonnelEntry(Personel personel, List<TurnstileRegistrationLog> logs);
    
    // Aylık personel listesi metodları
    HashMap<String, List<DtoDailyPersonnelEntry>> getMonthlyMainEntrancePersonnelList(YearMonth yearMonth);
    
    HashMap<String, List<DtoDailyPersonnelEntry>> getMonthlyMainEntrancePersonnelListFromDatabase(YearMonth yearMonth);
    
    HashMap<String, List<DtoDailyPersonnelEntry>> getMonthlyMainEntrancePersonnelListWithRedis(YearMonth yearMonth);
}
