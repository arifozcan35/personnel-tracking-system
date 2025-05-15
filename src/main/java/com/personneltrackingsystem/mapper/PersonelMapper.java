package com.personneltrackingsystem.mapper;

import com.personneltrackingsystem.dto.DtoPersonel;
import com.personneltrackingsystem.dto.DtoPersonelIU;
import com.personneltrackingsystem.dto.DtoPersonelTypeIU;
import com.personneltrackingsystem.dto.DtoUnitIU;
import com.personneltrackingsystem.entity.Personel;
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
    
    @Mapping(source = "personelTypeId", target = "personelTypeId", qualifiedByName = "personelTypeToId")
    @Mapping(source = "unitId", target = "unitId", qualifiedByName = "unitListToIdList")
    DtoPersonelIU personelToDtoPersonelIU(Personel personel);

    // DTO to Entity mappings
    @Mapping(source = "personelTypeId", target = "personelTypeId", qualifiedByName = "idToPersonelType")
    @Mapping(source = "unitId", target = "unitId", qualifiedByName = "idListToUnitList")
    Personel dtoPersonelIUToPersonel(DtoPersonelIU dtoPersonelIU);
    
    // Helper methods
    WorkingHours DbWorktoWork(Optional<WorkingHours> dbWorkOpt);

    // Converter methods
    DtoPersonelTypeIU map(Long personelTypeId);

    DtoUnitIU map2(Long unitId);

    List<DtoUnitIU> map2(List<Long> unitId);
    
    @Named("personelTypeToId")
    default Long personelTypeToId(DtoPersonelTypeIU personelTypeIU) {
        return personelTypeIU != null ? personelTypeIU.getPersonelTypeId() : null;
    }
    
    @Named("unitListToIdList")
    default List<Long> unitListToIdList(List<DtoUnitIU> units) {
        return units != null ? units.stream()
                .map(DtoUnitIU::getUnitId)
                .collect(Collectors.toList()) : null;
    }
    
    @Named("idToPersonelType")
    default DtoPersonelTypeIU idToPersonelType(Long id) {
        if (id == null) {
            return null;
        }
        DtoPersonelTypeIU type = new DtoPersonelTypeIU();
        type.setPersonelTypeId(id);
        return type;
    }
    
    @Named("idListToUnitList")
    default List<DtoUnitIU> idListToUnitList(List<Long> ids) {
        return ids != null ? ids.stream()
                .map(id -> {
                    DtoUnitIU unit = new DtoUnitIU();
                    unit.setUnitId(id);
                    return unit;
                })
                .collect(Collectors.toList()) : null;
    }
}
