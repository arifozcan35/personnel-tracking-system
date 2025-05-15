package com.personneltrackingsystem.controller.Impl;

import com.personneltrackingsystem.controller.PersonelTypeController;
import com.personneltrackingsystem.dto.DtoPersonelType;
import com.personneltrackingsystem.dto.DtoPersonelTypeIU;
import com.personneltrackingsystem.service.PersonelTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PersonelTypeControllerImpl implements PersonelTypeController {

    private final PersonelTypeService personelTypeService;

    @Override
    public List<DtoPersonelType> getAllPersonelTypes() {
        return personelTypeService.getAllPersonelTypes();
    }

    @Override
    public DtoPersonelType getOnePersonelType(Long personelTypeId) {
        return personelTypeService.getOnePersonelType(personelTypeId);
    }

    @Override
    public DtoPersonelType createPersonelType(DtoPersonelTypeIU newPersonelType) {
        return personelTypeService.saveOnePersonelType(newPersonelType);
    }

    @Override
    public DtoPersonelType updatePersonelType(Long personelTypeId, DtoPersonelTypeIU newPersonelType) {
        return personelTypeService.updateOnePersonelType(personelTypeId, newPersonelType);
    }

    @Override
    public void deletePersonelType(Long personelTypeId) {
        personelTypeService.deleteOnePersonelType(personelTypeId);
    }
} 