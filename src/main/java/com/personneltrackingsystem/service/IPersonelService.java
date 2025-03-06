package com.personneltrackingsystem.service;

import com.personneltrackingsystem.entity.Personel;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IPersonelService {

    public List<Personel> getAllPersonels();

    public Personel getAOnePersonel(Long personelId);

    public ResponseEntity<String> saveOnePersonel(Personel newPersonel);

    public ResponseEntity<String> updateOnePersonel(Long id, Personel newPersonel);

    public void deleteOnePersonel(Long id);

    public void workHoursCalculate(Long personelId);
}
