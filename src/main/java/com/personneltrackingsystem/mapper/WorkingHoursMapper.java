package com.personneltrackingsystem.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.personneltrackingsystem.dto.DtoPersonelTypeIU;
import com.personneltrackingsystem.dto.DtoWorkingHours;
import com.personneltrackingsystem.dto.DtoWorkingHoursIU;
import com.personneltrackingsystem.entity.WorkingHours;

import java.util.List;
import java.util.Optional;

@Mapper(componentModel = "spring")
public interface WorkingHoursMapper {

    // Entity to DTO mappings
    @Mapping(source = "personelTypeId", target = "personelTypeId", qualifiedByName = "personelTypeToId")
    DtoWorkingHours workingHoursToDtoWorkingHours(WorkingHours workingHours);
    
    @Mapping(source = "personelTypeId", target = "personelTypeId", qualifiedByName = "personelTypeToId")
    DtoWorkingHoursIU workingHoursToDtoWorkingHoursIU(WorkingHours workingHours);
    
    List<DtoWorkingHours> workingHoursListToDtoWorkingHoursList(List<WorkingHours> workingHoursList);
    
    // DTO to Entity mappings
    @Mapping(source = "personelTypeId", target = "personelTypeId", qualifiedByName = "idToPersonelType")
    WorkingHours dtoWorkingHoursToWorkingHours(DtoWorkingHours dtoWorkingHours);
    
    @Mapping(source = "personelTypeId", target = "personelTypeId", qualifiedByName = "idToPersonelType")
    WorkingHours dtoWorkingHoursIUToWorkingHours(DtoWorkingHoursIU dtoWorkingHoursIU);
    
    // Helper for Optional unwrapping (similar to what's in PersonelMapper)
    default WorkingHours unwrapOptional(Optional<WorkingHours> optionalWorkingHours) {
        return optionalWorkingHours.orElse(null);
    }
    
    // Converter methods for mapping between DtoPersonelTypeIU and Long
    @Named("personelTypeToId")
    default Long personelTypeToId(DtoPersonelTypeIU personelTypeIU) {
        return personelTypeIU != null ? personelTypeIU.getPersonelTypeId() : null;
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
}
