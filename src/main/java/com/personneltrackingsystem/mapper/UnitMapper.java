package com.personneltrackingsystem.mapper;

import com.personneltrackingsystem.dto.DtoUnit;
import com.personneltrackingsystem.dto.DtoUnitIU;
import com.personneltrackingsystem.entity.Unit;
import com.personneltrackingsystem.entity.Personel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UnitMapper {

    @Mapping(source = "unitName", target = "birimIsim")
    DtoUnit unitToDtoUnit(Unit unit);

    @Mapping(source = "birimIsim", target = "unitName")
    Unit dtoUnitToUnit(DtoUnit dtoUnit);

    List<DtoUnit> unitListToDtoUnitList(List<Unit> unitList);
    
    @Mapping(target = "floorId.floorId", source = "floorId")
    @Mapping(target = "administratorPersonelId", source = "administratorPersonelId", qualifiedByName = "longToPersonel")
    Unit dtoUnitIUToUnit(DtoUnitIU dtoUnitIU);

    @Mapping(target = "floorId", source = "floorId.floorId")
    @Mapping(target = "administratorPersonelId", source = "administratorPersonelId", qualifiedByName = "personelToLong")
    DtoUnitIU unitToDtoUnitIU(Unit unit);
    
    @Named("personelToLong")
    default Long personelToLong(Personel personel) {
        return personel != null ? personel.getPersonelId() : null;
    }
    
    @Named("longToPersonel")
    default Personel longToPersonel(Long personelId) {
        if (personelId == null) {
            return null;
        }
        Personel personel = new Personel();
        personel.setPersonelId(personelId);
        return personel;
    }
}
