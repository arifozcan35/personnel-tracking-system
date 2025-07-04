package com.personneltrackingsystem.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import com.personneltrackingsystem.dto.DtoTurnstile;
import com.personneltrackingsystem.dto.DtoTurnstileIU;
import com.personneltrackingsystem.entity.Turnstile;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TurnstileMapper {

    List<DtoTurnstile> turnstileListToDtoTurnstileList(List<Turnstile> turnstileList);

    DtoTurnstile turnstileToDtoTurnstile(Turnstile turnstile);

    @Mapping(target = "gateId.gateId", source = "gateId")
    Turnstile dtoTurnstileIUToTurnstile(DtoTurnstileIU dtoTurnstileIU);

}
