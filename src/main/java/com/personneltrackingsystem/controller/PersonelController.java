package com.personneltrackingsystem.controller;

import com.personneltrackingsystem.dto.DtoPersonel;
import com.personneltrackingsystem.entity.Personel;
import com.personneltrackingsystem.entity.Work;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;

public interface PersonelController {

    // solid example : article 4 (Interface Substitution Principle)

    List<DtoPersonel> getAllPersonels();

    DtoPersonel getOnePersonel(Long personelId);

    ResponseEntity<String> createPersonel(Personel newPersonel);

    ResponseEntity<String> updatePersonel(Long personelId, Personel newPersonel);

    void deletePersonel(Long personelId);

    DtoPersonel seeSalary(Long personelId);

    Work seeWork(Long personelId);

    Map<String, Double> getAllSalaries();
}
