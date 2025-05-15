package com.personneltrackingsystem.mapper;

import com.personneltrackingsystem.dto.DtoUnit;
import com.personneltrackingsystem.entity.Unit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UnitMapper {

    @Mapping(source = "unitName", target = "birimIsim")
    DtoUnit unitToDtoUnit(Unit unit);

    @Mapping(source = "birimIsim", target = "unitName")
    Unit dtoUnitToUnit(DtoUnit dtoUnit);


    List<DtoUnit> unitListToDtoUnitList(List<Unit> unitList);

}
