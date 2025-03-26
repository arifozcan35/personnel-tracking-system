package com.personneltrackingsystem.controller;

import com.personneltrackingsystem.dto.DtoPersonel;
import com.personneltrackingsystem.dto.DtoPersonelIU;
import com.personneltrackingsystem.entity.Personel;
import com.personneltrackingsystem.entity.Work;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface PersonelController {

    // solid example : article 4 (Interface Substitution Principle)

    List<DtoPersonel> getAllPersonels();

    DtoPersonel getOnePersonel(Long personelId);

    ResponseEntity<String> createPersonel(DtoPersonelIU newPersonel);

    ResponseEntity<String> updatePersonel(Long personelId, DtoPersonelIU newPersonel);

    void deletePersonel(Long personelId);

    DtoPersonel seeSalary(Long personelId);

    Work seeWork(Long personelId);

    Map<String, Double> getAllSalaries();
}
