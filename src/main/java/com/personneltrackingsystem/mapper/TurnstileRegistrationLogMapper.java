package com.personneltrackingsystem.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.personneltrackingsystem.dto.DtoTurnstileRegistrationLog;
import com.personneltrackingsystem.entity.TurnstileRegistrationLog;

@Mapper(componentModel = "spring")
public interface TurnstileRegistrationLogMapper {


    TurnstileRegistrationLog dtoTurnstileRegistrationLogToTurnstileRegistrationLog(DtoTurnstileRegistrationLog dtoTurnstileRegistrationLog);

    List<DtoTurnstileRegistrationLog> turnstileRegistrationLogListToDtoTurnstileRegistrationLogList(List<TurnstileRegistrationLog> turnstileRegistrationLogList);

    DtoTurnstileRegistrationLog turnstileRegistrationLogToDtoTurnstileRegistrationLog(TurnstileRegistrationLog turnstileRegistrationLog);

}
