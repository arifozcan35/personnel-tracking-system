package com.personneltrackingsystem.controller;

import com.personneltrackingsystem.dto.DtoGate;
import com.personneltrackingsystem.dto.DtoGateIU;
import com.personneltrackingsystem.entity.Personel;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IGateController {

    // solid example : article 4 (Interface Substitution Principle)

    public List<DtoGate> getAllGates();

    public DtoGate getOneGate(Long gateId);

    public DtoGate createGate(DtoGateIU newGate);

    public DtoGate updateGate(Long gateId, DtoGateIU newGate);

    public void deleteGate(Long gateId);

    public ResponseEntity<String> passGise(Long wantedToEnterGate, Personel personel);
}
