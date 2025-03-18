package com.personneltrackingsystem.controller;

import com.personneltrackingsystem.dto.DtoGate;
import com.personneltrackingsystem.dto.DtoGateIU;
import com.personneltrackingsystem.entity.Personel;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Set;

public interface GateController {

    List<DtoGate> getAllGates();

    DtoGate getOneGate(Long gateId);

    DtoGate createGate(DtoGateIU newGate);

    DtoGate updateGate(Long gateId, DtoGateIU newGate);

    void deleteGate(Long gateId);

    Set<Personel> getPersonels(Long unitId);

    ResponseEntity<String> passGate(Long wantedToEnterGate, Personel personel);
}
