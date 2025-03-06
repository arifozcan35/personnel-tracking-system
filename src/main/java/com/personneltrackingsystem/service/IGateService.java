package com.personneltrackingsystem.service;

import com.personneltrackingsystem.entity.Gate;
import com.personneltrackingsystem.entity.Personel;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IGateService {

    public List<Gate> getAllGates();

    public Gate getOneGate(Long gateId);

    public Gate saveOneGate(Gate gate);

    public Gate updateOneGate(Long id, Gate yeniGate);

    public void deleteOneGate(Long gateId);

    public ResponseEntity<String> passGise(Long wantedToEnterGate, Personel personel);


}
