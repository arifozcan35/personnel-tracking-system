package com.personneltrackingsystem.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.personneltrackingsystem.dto.DtoTurnstile;
import com.personneltrackingsystem.dto.DtoTurnstileIU;
import com.personneltrackingsystem.entity.Turnstile;

@Mapper(componentModel = "spring")
public interface TurnstileMapper {

    Turnstile dtoTurnstileIUToTurnstile(DtoTurnstileIU dtoTurnstileIU);

    Turnstile dtoTurnstileToTurnstile(DtoTurnstile dtoTurnstile);

    List<DtoTurnstile> turnstileListToDtoTurnstileList(List<Turnstile> turnstileList);

    DtoTurnstile turnstileToDtoTurnstile(Turnstile turnstile);

    DtoTurnstileIU turnstileToDtoTurnstileIU(Turnstile turnstile);
}
