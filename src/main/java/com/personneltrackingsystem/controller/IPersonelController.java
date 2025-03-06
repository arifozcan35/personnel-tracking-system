package com.personneltrackingsystem.controller;

import com.personneltrackingsystem.entity.Personel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface IPersonelController {

    public List<Personel> getAllPersonels();

    public Personel getOnePersonel(Long personelId);

    public ResponseEntity<String> createPersonel(Personel newPersonel);

    public ResponseEntity<String> updatePersonel(Long personelId, Personel newPersonel);

    public void deletePersonel(Long personelId);

    public void seeSalary(Long personelId);
}
