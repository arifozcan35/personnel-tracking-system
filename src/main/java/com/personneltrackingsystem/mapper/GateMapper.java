package com.personneltrackingsystem.mapper;

import com.personneltrackingsystem.dto.DtoGate;
import com.personneltrackingsystem.dto.DtoGateIU;
import com.personneltrackingsystem.entity.Gate;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GateMapper {

    DtoGate gateToDtoGate(Gate gate);

    List<DtoGate> gatesToDtoGates(List<Gate> gateList);

    Gate dtoGateIUToGate(DtoGateIU dtoGateIU);

}
