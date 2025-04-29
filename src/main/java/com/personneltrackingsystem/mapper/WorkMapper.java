package com.personneltrackingsystem.mapper;

import com.personneltrackingsystem.dto.DtoWork;
import com.personneltrackingsystem.dto.DtoWorkIU;
import com.personneltrackingsystem.entity.Work;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
    public interface WorkMapper {

    Work dtoWorkToWork(DtoWork dtoWork);

    DtoWork workToDtoWork(Work work);

    Work dtoWorkIUToWork(DtoWorkIU dtoWorkIU);

    DtoWorkIU workToDtoWorkIU(Work work);


    List<DtoWorkIU> workListToDtoWorkIUList(List<Work> workList);

    List<Work> DtoWorkListToWork(List<DtoWork> workList);
}
