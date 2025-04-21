package com.personneltrackingsystem.mapper;

import com.personneltrackingsystem.dto.DtoUnit;
import com.personneltrackingsystem.dto.DtoUnitIU;
import com.personneltrackingsystem.entity.Unit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UnitMapper {

    @Mapping(source = "unitName", target = "birimIsim")
    DtoUnit unitToDtoUnit(Unit unit);

    @Mapping(source = "birimIsim", target = "unitName")
    Unit dtoUnitIUToUnit(DtoUnitIU dtoUnitIU);

    @Mapping(source = "unitName", target = "birimIsim")
    DtoUnitIU unitToDtoUnitIU(Unit unit);

    @Mapping(source = "birimIsim", target = "unitName")
    Unit dtoUnitToUnit(DtoUnit dtoUnit);

}
