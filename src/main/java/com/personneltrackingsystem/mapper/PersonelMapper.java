package com.personneltrackingsystem.mapper;

import com.personneltrackingsystem.dto.DtoPersonel;
import com.personneltrackingsystem.dto.DtoPersonelAll;
import com.personneltrackingsystem.dto.DtoPersonelIU;
import com.personneltrackingsystem.dto.DtoPersonelType;
import com.personneltrackingsystem.dto.DtoUnitIU;
import com.personneltrackingsystem.entity.Personel;
import com.personneltrackingsystem.entity.PersonelType;
import com.personneltrackingsystem.entity.Unit;
import com.personneltrackingsystem.entity.WorkingHours;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {UnitMapper.class})
public interface PersonelMapper {

    // Entity to DTO mappings
    List<DtoPersonel> personelsToDtoPersonels(List<Personel> personelList);

    @Mapping(source = "name", target = "name")
    @Mapping(source = "email", target = "email")
    DtoPersonel personelToDtoPersonel(Personel personel);
    
    @Mapping(source = "personelTypeId", target = "personelTypeId", qualifiedByName = "personelTypeEntityToLong")
    @Mapping(source = "unitId", target = "unitId", qualifiedByName = "unitEntityListToLongList")
    DtoPersonelIU personelToDtoPersonelIU(Personel personel);

    // DTO to Entity mappings
    @Mapping(source = "personelTypeId", target = "personelTypeId", qualifiedByName = "longToPersonelTypeEntity")
    @Mapping(source = "unitId", target = "unitId", qualifiedByName = "longListToUnitEntityList")
    Personel dtoPersonelIUToPersonel(DtoPersonelIU dtoPersonelIU);

    @Mapping(source = "personelTypeId", target = "personelType", qualifiedByName = "personelTypeEntityToDtoPersonelType")
    @Mapping(source = "unitId", target = "units", qualifiedByName = "unitListToDtoUnitIUList")
    DtoPersonelAll personelToDtoPersonelAll(Personel personel);

    
    @Mapping(target = "personelTypeId", ignore = true)
    @Mapping(target = "unitId", ignore = true)
    @Mapping(target = "personelId", ignore = true)
    Personel dtoPersonelToPersonel(DtoPersonel dtoPersonel);
    
    // Helper methods
    WorkingHours DbWorktoWork(Optional<WorkingHours> dbWorkOpt);

    @Named("personelTypeEntityToLong")
    default Long personelTypeEntityToLong(PersonelType personelType) {
        return personelType != null ? personelType.getPersonelTypeId() : null;
    }
    
    @Named("unitEntityListToLongList")
    default List<Long> unitEntityListToLongList(List<Unit> units) {
        return units != null ? units.stream()
                .map(Unit::getUnitId)
                .collect(Collectors.toList()) : null;
    }
    
    @Named("longToPersonelTypeEntity")
    default PersonelType longToPersonelTypeEntity(Long id) {
        if (id == null) {
            return null;
        }
        PersonelType type = new PersonelType();
        type.setPersonelTypeId(id);
        return type;
    }
    
    @Named("longListToUnitEntityList")
    default List<Unit> longListToUnitEntityList(List<Long> ids) {
        return ids != null ? ids.stream()
                .map(id -> {
                    Unit unit = new Unit();
                    unit.setUnitId(id);
                    return unit;
                })
                .collect(Collectors.toList()) : null;
    }

    @Named("unitListToDtoUnitIUList")
    default List<DtoUnitIU> unitListToDtoUnitIUList(List<Unit> units) {
        if (units == null) {
            return null;
        }
        
        return units.stream().map(unit -> {
            DtoUnitIU dto = new DtoUnitIU();
            dto.setBirimIsim(unit.getUnitName());
            
            if (unit.getFloorId() != null) {
                dto.setFloorId(unit.getFloorId().getFloorId());
            }
            
            if (unit.getAdministratorPersonelId() != null) {
                dto.setAdministratorPersonelId(unit.getAdministratorPersonelId().getPersonelId());
            }
            
            return dto;
        }).collect(Collectors.toList());
    }

    @Named("personelTypeEntityToDtoPersonelType")
    default DtoPersonelType personelTypeEntityToDtoPersonelType(PersonelType personelType) {
        if (personelType == null) {
            return null;
        }
        
        DtoPersonelType dto = new DtoPersonelType();
        dto.setPersonelTypeName(personelType.getPersonelTypeName());
        return dto;
    }

}
