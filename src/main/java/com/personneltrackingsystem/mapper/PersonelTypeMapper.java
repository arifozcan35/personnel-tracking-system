package com.personneltrackingsystem.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.personneltrackingsystem.dto.DtoPersonelType;
import com.personneltrackingsystem.entity.PersonelType;

@Mapper(componentModel = "spring")
public interface PersonelTypeMapper {

    PersonelType dtoPersonelTypeToPersonelType(DtoPersonelType dtoPersonelType);

    List<DtoPersonelType> personelTypeListToDtoPersonelTypeList(List<PersonelType> personelTypeList);

    DtoPersonelType personelTypeToDtoPersonelType(PersonelType personelType);
    
}
