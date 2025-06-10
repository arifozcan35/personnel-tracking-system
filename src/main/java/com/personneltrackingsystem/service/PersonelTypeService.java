package com.personneltrackingsystem.service;

import com.personneltrackingsystem.dto.DtoPersonelType;

import java.util.List;
import java.util.Optional;

public interface PersonelTypeService {

    List<DtoPersonelType> getAllPersonelTypes();

    Optional<DtoPersonelType> getPersonelTypeById(Long id);

    DtoPersonelType getOnePersonelType(Long id);

    DtoPersonelType saveOnePersonelType(DtoPersonelType newPersonelType);

    DtoPersonelType updateOnePersonelType(Long id, DtoPersonelType newPersonelType);
    
    void deleteOnePersonelType(Long id);
    
    void initializeDefaultPersonelTypes();
} 