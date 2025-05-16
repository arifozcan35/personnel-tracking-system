package com.personneltrackingsystem.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.personneltrackingsystem.dto.DtoWorkingHours;
import com.personneltrackingsystem.dto.DtoWorkingHoursIU;
import com.personneltrackingsystem.entity.PersonelType;
import com.personneltrackingsystem.entity.WorkingHours;

import java.util.List;

@Mapper(componentModel = "spring")
public interface WorkingHoursMapper {

    @Mapping(target = "personelTypeId", source = "personelTypeId", qualifiedByName = "longToPersonelType")
    WorkingHours dtoWorkingHoursIUToWorkingHours(DtoWorkingHoursIU dtoWorkingHoursIU);

    @Mapping(target = "personelTypeId", source = "personelTypeId", qualifiedByName = "personelTypeToLong")
    DtoWorkingHoursIU workingHoursToDtoWorkingHoursIU(WorkingHours workingHours);
    
    @Mapping(target = "personelTypeId", source = "personelTypeId", qualifiedByName = "longToPersonelType")
    WorkingHours dtoWorkingHoursToWorkingHours(DtoWorkingHours dtoWorkingHours);

    List<DtoWorkingHours> workingHoursListToDtoWorkingHoursList(List<WorkingHours> workingHoursList);

    @Mapping(target = "personelTypeId", source = "personelTypeId", qualifiedByName = "personelTypeToLong")
    DtoWorkingHours workingHoursToDtoWorkingHours(WorkingHours workingHours);
    
    @Named("personelTypeToLong")
    default Long personelTypeToLong(PersonelType personelType) {
        return personelType != null ? personelType.getPersonelTypeId() : null;
    }
    
    @Named("longToPersonelType")
    default PersonelType longToPersonelType(Long personelTypeId) {
        if (personelTypeId == null) {
            return null;
        }
        PersonelType personelType = new PersonelType();
        personelType.setPersonelTypeId(personelTypeId);
        return personelType;
    }
}
