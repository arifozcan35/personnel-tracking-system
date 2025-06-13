package com.personneltrackingsystem.mapper;

import com.personneltrackingsystem.dto.DtoGate;
import com.personneltrackingsystem.dto.DtoGateIU;
import com.personneltrackingsystem.entity.Gate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GateMapper {

    DtoGate gateToDtoGate(Gate gate);


    List<DtoGate> gateListToDtoGateList(List<Gate> gateList);

    @Mapping(target = "unitId.unitId", source = "unitId")
    Gate dtoGateIUToGate(DtoGateIU dtoGateIU);

}
