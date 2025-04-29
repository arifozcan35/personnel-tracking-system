package com.personneltrackingsystem.mapper;

import com.personneltrackingsystem.dto.DtoWork;
import com.personneltrackingsystem.dto.DtoWorkIU;
import com.personneltrackingsystem.entity.Work;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
    public interface WorkMapper {

    Work dtoWorkToWork(DtoWork dtoWork);

    DtoWork workToDtoWork(Work work);

    Work dtoWorkIUToWork(DtoWorkIU dtoWorkIU);

    DtoWorkIU workToDtoWorkIU(Work work);

}
