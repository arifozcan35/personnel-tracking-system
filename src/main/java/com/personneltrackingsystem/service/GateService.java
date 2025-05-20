package com.personneltrackingsystem.service;

import com.personneltrackingsystem.dto.DtoGate;
import com.personneltrackingsystem.dto.DtoGateIU;
import com.personneltrackingsystem.entity.Gate;

import java.util.List;
import java.util.Optional;

public interface GateService {
    
    Gate checkIfGateExists(Long gateId);

    List<DtoGate> getAllGates();

    Optional<DtoGate> getGateById(Long id);

    DtoGate getOneGate(Long gateId);

    DtoGate saveOneGate(DtoGateIU newGate);

    DtoGate updateOneGate(Long id, DtoGateIU newGate);

    void deleteOneGate(Long gateId);
} 