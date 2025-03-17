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

    public List<DtoPersonel> getAllPersonels();

    public DtoPersonel getOnePersonel(Long personelId);

    public ResponseEntity<String> createPersonel(Personel newPersonel);

    public ResponseEntity<String> updatePersonel(Long personelId, Personel newPersonel);

    public void deletePersonel(Long personelId);

    public DtoPersonel seeSalary(Long personelId);

    public Work seeWork(Long personelId);

    public Map<String, Double> getAllSalaries();
}
