package com.personneltrackingsystem.controller.Impl;

import com.personneltrackingsystem.controller.PersonelController;
import com.personneltrackingsystem.dto.DtoPersonel;
import com.personneltrackingsystem.dto.DtoPersonelAll;
import com.personneltrackingsystem.dto.DtoPersonelIU;
import com.personneltrackingsystem.service.PersonelService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
public class PersonelControllerImpl implements PersonelController {

    private final PersonelService personelServiceImpl;

    @Override
    public List<DtoPersonel> getAllPersonels() {
        return personelServiceImpl.getAllPersonels();
    }

    @Override
    public DtoPersonelAll getOnePersonel(Long personelId){
        return personelServiceImpl.getAOnePersonel(personelId);
    }

    @Override
    public ResponseEntity<String> createPersonel(DtoPersonelIU newPersonel) {
        return personelServiceImpl.saveOnePersonel(newPersonel);
    }

    @Override
    public ResponseEntity<String> updatePersonel(Long personelId, DtoPersonelIU newPersonel) {
        return personelServiceImpl.updateOnePersonel(personelId, newPersonel);
    }

    @Override
    public void deletePersonel(Long personelId) {
        personelServiceImpl.deleteOnePersonel(personelId);
    }

    @Override
    public Set<DtoPersonel> getPersonelsByUnit(Long unitId) {
        return personelServiceImpl.getPersonelsByUnitId(unitId);
    }
}
