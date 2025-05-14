package com.personneltrackingsystem.service;

import com.personneltrackingsystem.dto.DtoPersonelType;
import com.personneltrackingsystem.dto.DtoPersonelTypeIU;

import java.util.List;
import java.util.Optional;

public interface PersonelTypeService {

    List<DtoPersonelType> getAllPersonelTypes();
    Optional<DtoPersonelType> getPersonelTypeById(Long id);
    DtoPersonelType getOnePersonelType(Long id);
    DtoPersonelType saveOnePersonelType(DtoPersonelTypeIU personelTypeDto);
    DtoPersonelType updateOnePersonelType(Long id, DtoPersonelTypeIU personelTypeDto);
    void deleteOnePersonelType(Long id);
} 