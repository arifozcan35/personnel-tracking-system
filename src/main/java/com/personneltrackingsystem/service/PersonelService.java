package com.personneltrackingsystem.service;

import com.personneltrackingsystem.dto.DtoPersonel;
import com.personneltrackingsystem.entity.Gate;
import com.personneltrackingsystem.entity.Personel;
import com.personneltrackingsystem.entity.Unit;
import com.personneltrackingsystem.entity.Work;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface PersonelService {

    // solid example : article 4 (Interface Substitution Principle)

    /**
     *
     * @return
     */


    List<DtoPersonel> getAllPersonels();

    DtoPersonel getAOnePersonel(Long personelId);

    ResponseEntity<String> saveOnePersonel(Personel newPersonel);

    ResponseEntity<String> updateOnePersonel(Long id, Personel newPersonel);

    void deleteOnePersonel(Long id);

    DtoPersonel calculateSalaryByPersonelId(Long personelId);

    DtoPersonel workHoursCalculate(Long personelId);

    void workHoursCalculate2(Personel newPersonel);

    Work getOneWorkofPersonel(Long personelId);

    Map<String, Double> listSalaries();
}
