package com.personneltrackingsystem.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.personneltrackingsystem.dto.DtoSalary;
import com.personneltrackingsystem.entity.Salary;

@Mapper(componentModel = "spring")
public interface SalaryMapper {

    @Mapping(source = "personelId.personelId", target = "personelId")
    @Mapping(source = "personelId.name", target = "personelName")
    @Mapping(source = "personelId.email", target = "personelEmail")
    @Mapping(source = "personelId.personelTypeId.personelTypeName", target = "personelTypeName")
    DtoSalary salaryToDtoSalary(Salary salary);
    
    List<DtoSalary> salaryListToDtoSalaryList(List<Salary> salaryList);
} 