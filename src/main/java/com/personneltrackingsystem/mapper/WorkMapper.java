package com.personneltrackingsystem.mapper;

import com.personneltrackingsystem.dto.DtoWork;
import com.personneltrackingsystem.entity.Work;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
    public interface WorkMapper {

    Work dtoWorkToWork(DtoWork dtoWork);

}
