package com.personneltrackingsystem.controller;

import com.personneltrackingsystem.dto.DtoGate;
import com.personneltrackingsystem.dto.DtoGateIU;
import com.personneltrackingsystem.dto.DtoPersonel;
import com.personneltrackingsystem.dto.DtoPersonelIU;
import com.personneltrackingsystem.entity.Personel;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface GateController {

    public List<DtoGate> getAllGates();

    public DtoGate getOneGate(Long gateId);

    public DtoGate createGate(DtoGateIU newGate);

    public DtoGate updateGate(Long gateId, DtoGateIU newGate);

    public void deleteGate(Long gateId);

    public List<Personel> getPersonels(Long unitId);

    public ResponseEntity<String> passGate(Long wantedToEnterGate, Personel personel);
}
