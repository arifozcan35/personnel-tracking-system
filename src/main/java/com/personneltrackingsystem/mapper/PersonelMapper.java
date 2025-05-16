package com.personneltrackingsystem.mapper;

import com.personneltrackingsystem.dto.DtoPersonel;
import com.personneltrackingsystem.dto.DtoPersonelIU;
import com.personneltrackingsystem.dto.DtoPersonelTypeIU;
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

@Mapper(componentModel = "spring")
public interface PersonelMapper {

    // Entity to DTO mappings
    List<DtoPersonel> personelsToDtoPersonels(List<Personel> personelList);

    DtoPersonel personelToDtoPersonel(Personel personel);
    
    @Mapping(source = "personelTypeId", target = "personelTypeId", qualifiedByName = "personelTypeEntityToLong")
    @Mapping(source = "unitId", target = "unitId", qualifiedByName = "unitEntityListToLongList")
    DtoPersonelIU personelToDtoPersonelIU(Personel personel);

    // DTO to Entity mappings
    @Mapping(source = "personelTypeId", target = "personelTypeId", qualifiedByName = "longToPersonelTypeEntity")
    @Mapping(source = "unitId", target = "unitId", qualifiedByName = "longListToUnitEntityList")
    Personel dtoPersonelIUToPersonel(DtoPersonelIU dtoPersonelIU);
    
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
}
