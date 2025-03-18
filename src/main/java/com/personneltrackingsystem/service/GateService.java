package com.personneltrackingsystem.service;

import com.personneltrackingsystem.dto.DtoGate;
import com.personneltrackingsystem.dto.DtoGateIU;
import com.personneltrackingsystem.entity.Gate;
import com.personneltrackingsystem.entity.Personel;
import com.personneltrackingsystem.entity.Unit;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface GateService {

    Optional<Gate> findById(Long gateId);

    List<DtoGate> getAllGates();

    DtoGate getOneGate(Long gateId);

    DtoGate saveOneGate(DtoGateIU gate);

    DtoGate updateOneGate(Long id, DtoGateIU newGate);

    void deleteOneGate(Long gateId);

    Set<Personel> getPersonelsByGateId(Long gateId);

    ResponseEntity<String> passGate(Long wantedToEnterGate, Personel personel);


}
