package com.personneltrackingsystem.controller.Impl;

import com.personneltrackingsystem.controller.PersonelController;
import com.personneltrackingsystem.dto.DtoPersonel;
import com.personneltrackingsystem.dto.DtoPersonelIU;
import com.personneltrackingsystem.entity.Work;
import com.personneltrackingsystem.service.PersonelService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class PersonelControllerImpl implements PersonelController {

    private final PersonelService personelServiceImpl;


    @Override
    public List<DtoPersonel> getAllPersonels() {
        return personelServiceImpl.getAllPersonels();
    }


    @Override
    public DtoPersonel getOnePersonel(Long personelId){
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
    public DtoPersonel seeSalary(Long personelId) {
        return personelServiceImpl.calculateSalaryByPersonelId(personelId);
    }


    @Override
    public Work seeWork(Long personelId) {
        return personelServiceImpl.getOneWorkofPersonel(personelId);
    }


    @Override
    public Map<String, Double> getAllSalaries() {
        Map<String, Double> salaries = personelServiceImpl.listSalaries();
        return salaries;
    }

}
