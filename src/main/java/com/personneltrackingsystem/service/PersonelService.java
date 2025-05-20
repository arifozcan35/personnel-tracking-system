package com.personneltrackingsystem.service;

import com.personneltrackingsystem.dto.DtoPersonel;
import com.personneltrackingsystem.dto.DtoPersonelAll;
import com.personneltrackingsystem.dto.DtoPersonelIU;
import com.personneltrackingsystem.entity.Personel;

import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Set;

public interface PersonelService {

    // solid example : article 4 (Interface Substitution Principle)

    List<DtoPersonel> getAllPersonels();

    DtoPersonelAll getAOnePersonel(Long personelId);

    ResponseEntity<String> saveOnePersonel(DtoPersonelIU newPersonel);

    ResponseEntity<String> updateOnePersonel(Long id, DtoPersonelIU newPersonel);

    void deleteOnePersonel(Long id);

    Set<DtoPersonel> getPersonelsByUnitId(Long unitId);

    Personel checkIfPersonelExists(Long administratorPersonelId);

    DtoPersonel personelMapper(Personel personel);

    // Map<String, Double> listSalaries();
}
