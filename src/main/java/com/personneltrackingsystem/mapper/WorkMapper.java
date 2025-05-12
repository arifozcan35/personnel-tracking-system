package com.personneltrackingsystem.mapper;

import com.personneltrackingsystem.dto.DtoWork;
import com.personneltrackingsystem.dto.DtoWorkIU;
import com.personneltrackingsystem.entity.WorkingHours;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
    public interface WorkMapper {

    WorkingHours dtoWorkToWork(DtoWork dtoWork);

    DtoWork workToDtoWork(WorkingHours work);

    WorkingHours dtoWorkIUToWork(DtoWorkIU dtoWorkIU);

    DtoWorkIU workToDtoWorkIU(WorkingHours work);


    List<DtoWorkIU> workListToDtoWorkIUList(List<WorkingHours> workList);

    List<WorkingHours> DtoWorkListToWork(List<DtoWork> workList);
}
