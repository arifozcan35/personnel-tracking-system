package com.personneltrackingsystem.service;

import com.personneltrackingsystem.dto.DtoTurnstileRegistrationLogIU;
import com.personneltrackingsystem.dto.DtoTurnstileBasedPersonnelEntry;
import com.personneltrackingsystem.dto.DtoTurnstilePassageFullRequest;

import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface TurnstileRegistrationLogService {

    void saveOneTurnstileRegistrationLog(DtoTurnstileRegistrationLogIU dtoTurnstileRegistrationLogIU);


    HashMap<String, Map<String, List<DtoTurnstileBasedPersonnelEntry>>> getMonthlyTurnstileBasedPersonnelListFromHazelcast(YearMonth yearMonth);
    
    HashMap<String, Map<String, List<DtoTurnstileBasedPersonnelEntry>>> getMonthlyTurnstileBasedPersonnelListFromRedis(YearMonth yearMonth);
    

    YearMonth validateAndGetYearMonth(YearMonth yearMonth);

    void validateTurnstilePassage(DtoTurnstilePassageFullRequest request);
}
