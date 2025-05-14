package com.personneltrackingsystem.service;

import com.personneltrackingsystem.dto.DtoGate;
import com.personneltrackingsystem.dto.DtoGateIU;

import java.util.List;
import java.util.Optional;

public interface GateService {
    List<DtoGate> getAllGates();
    Optional<DtoGate> getGateById(Long id);
    DtoGate getOneGate(Long gateId);
    DtoGate saveOneGate(DtoGateIU gate);
    DtoGate updateOneGate(Long id, DtoGateIU newGate);
    void deleteOneGate(Long gateId);
} 