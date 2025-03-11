package com.personneltrackingsystem.service;

import com.personneltrackingsystem.dto.DtoPersonel;
import com.personneltrackingsystem.entity.Personel;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface PersonelService {

    // solid example : article 4 (Interface Substitution Principle)

    /**
     *
     * @return
     */

    public List<DtoPersonel> getAllPersonels();

    public DtoPersonel getAOnePersonel(Long personelId);

    public ResponseEntity<String> saveOnePersonel(Personel newPersonel);

    public ResponseEntity<String> updateOnePersonel(Long id, Personel newPersonel);

    public void deleteOnePersonel(Long id);

    public void workHoursCalculate(Long personelId);
}
