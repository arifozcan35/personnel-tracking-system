package com.personneltrackingsystem.mapper;

import com.personneltrackingsystem.dto.DtoUnit;
import com.personneltrackingsystem.dto.DtoUnitIU;
import com.personneltrackingsystem.entity.Unit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UnitMapper {

    UnitMapper INSTANCE = Mappers.getMapper(UnitMapper.class);

    @Mapping(source = "unitName", target = "birimIsim")
    DtoUnit unitToDtoUnit(Unit unit);

    @Mapping(source = "unitName", target = "birimIsim")
    List<DtoUnit> unitsToDtoUnits(List<Unit> unitList);

    @Mapping(source = "birimIsim", target = "unitName")
    Unit dtoUnitIUToUnit(DtoUnitIU dtoUnitIU);

}
