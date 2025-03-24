package com.personneltrackingsystem.mapper;

import com.personneltrackingsystem.dto.DtoUnit;
import com.personneltrackingsystem.dto.DtoUnitIU;
import com.personneltrackingsystem.entity.Unit;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UnitMapper {

    UnitMapper INSTANCE = Mappers.getMapper(UnitMapper.class);

    DtoUnit unitToDtoUnit(Unit unit);

    List<DtoUnit> unitsToDtoUnits(List<Unit> unitList);

    Unit dtoUnitIUToUnit(DtoUnitIU dtoUnitIU);
}
