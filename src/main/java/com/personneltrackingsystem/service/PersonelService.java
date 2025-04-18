package com.personneltrackingsystem.service;

import com.personneltrackingsystem.dto.DtoPersonel;
import com.personneltrackingsystem.dto.DtoPersonelIU;
import com.personneltrackingsystem.entity.Personel;
import com.personneltrackingsystem.entity.Work;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface PersonelService {

    // solid example : article 4 (Interface Substitution Principle)

    /**
     *
     * @return
     */

    List<DtoPersonel> getAllPersonels();

    DtoPersonel getAOnePersonel(Long personelId);

    ResponseEntity<String> saveOnePersonel(DtoPersonelIU newPersonel);

    ResponseEntity<String> updateOnePersonel(Long id, DtoPersonelIU newPersonel);

    void deleteOnePersonel(Long id);


    DtoPersonel workHoursCalculate(Long personelId);

    void workHoursCalculate2(Personel newPersonel);

    Work getOneWorkofPersonel(Long personelId);

    Map<String, Double> listSalaries();
}
