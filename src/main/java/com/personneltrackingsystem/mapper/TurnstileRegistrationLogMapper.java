package com.personneltrackingsystem.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import com.personneltrackingsystem.dto.DtoTurnstileRegistrationLog;
import com.personneltrackingsystem.dto.DtoTurnstileRegistrationLogIU;
import com.personneltrackingsystem.entity.Personel;
import com.personneltrackingsystem.entity.Turnstile;
import com.personneltrackingsystem.entity.TurnstileRegistrationLog;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TurnstileRegistrationLogMapper {

    DtoTurnstileRegistrationLog turnstileRegistrationLogToDtoTurnstileRegistrationLog(TurnstileRegistrationLog turnstileRegistrationLog);

    @Mapping(target = "personelId", source = "personelId", qualifiedByName = "longToPersonel")
    @Mapping(target = "turnstileId", source = "turnstileId", qualifiedByName = "longToTurnstile")
    TurnstileRegistrationLog dtoTurnstileRegistrationLogIUToTurnstileRegistrationLog(DtoTurnstileRegistrationLogIU dtoTurnstileRegistrationLogIU);
    
    @Named("longToPersonel")
    default Personel longToPersonel(Long personelId) {
        if (personelId == null) {
            return null;
        }
        Personel personel = new Personel();
        personel.setPersonelId(personelId);
        return personel;
    }
    
    @Named("longToTurnstile")
    default Turnstile longToTurnstile(Long turnstileId) {
        if (turnstileId == null) {
            return null;
        }
        Turnstile turnstile = new Turnstile();
        turnstile.setTurnstileId(turnstileId);
        return turnstile;
    }
}
