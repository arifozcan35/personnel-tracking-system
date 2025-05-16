package com.personneltrackingsystem.mapper;

import com.personneltrackingsystem.dto.DtoGate;
import com.personneltrackingsystem.dto.DtoGateIU;
import com.personneltrackingsystem.entity.Gate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GateMapper {

    DtoGate gateToDtoGate(Gate gate);

    List<DtoGate> gatesToDtoGates(List<Gate> gateList);

    Gate dtoGateToGate(DtoGate dtoGate);


    List<DtoGate> gateListToDtoGateList(List<Gate> gateList);

    @Mapping(target = "unitId.unitId", source = "unitId")
    Gate dtoGateIUToGate(DtoGateIU dtoGateIU);

    @Mapping(target = "unitId", source = "unitId.unitId")
    DtoGateIU gateToDtoGateIU(Gate gate);

}
