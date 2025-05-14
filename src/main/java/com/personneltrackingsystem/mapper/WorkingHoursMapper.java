package com.personneltrackingsystem.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.personneltrackingsystem.dto.DtoWorkingHours;
import com.personneltrackingsystem.dto.DtoWorkingHoursIU;
import com.personneltrackingsystem.entity.WorkingHours;

@Mapper(componentModel = "spring")
public interface WorkingHoursMapper {

    WorkingHours dtoWorkingHoursIUToWorkingHours(DtoWorkingHoursIU dtoWorkingHoursIU);

    DtoWorkingHoursIU workingHoursToDtoWorkingHoursIU(WorkingHours workingHours);

    List<DtoWorkingHours> workingHoursListToDtoWorkingHoursList(List<WorkingHours> workingHoursList);

    DtoWorkingHours workingHoursToDtoWorkingHours(WorkingHours workingHours);

    WorkingHours dtoWorkingHoursToWorkingHours(DtoWorkingHours dtoWorkingHours);

    List<WorkingHours> dtoWorkingHoursListToWorkingHoursList(List<DtoWorkingHours> dtoWorkingHoursList);
}
