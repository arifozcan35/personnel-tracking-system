package com.personneltrackingsystem.mapper;

import com.personneltrackingsystem.dto.DtoGate;
import com.personneltrackingsystem.dto.DtoGateIU;
import com.personneltrackingsystem.entity.Gate;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GateMapper {

    DtoGate gateToDtoGate(Gate gate);

    List<DtoGate> gatesToDtoGates(List<Gate> gateList);

    Gate dtoGateIUToGate(DtoGateIU dtoGateIU);

    DtoGateIU gateToDtoGateIU(Gate gate);

    Gate dtoGateToGate(DtoGate dtoGate);

}
