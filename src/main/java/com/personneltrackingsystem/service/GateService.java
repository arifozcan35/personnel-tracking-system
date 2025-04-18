package com.personneltrackingsystem.service;

import com.personneltrackingsystem.dto.DtoGate;
import com.personneltrackingsystem.dto.DtoGateIU;
import com.personneltrackingsystem.entity.Gate;
import com.personneltrackingsystem.entity.Personel;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Validated
public interface GateService {

    DtoGate findById(Long gateId);

    List<DtoGate> getAllGates();

    DtoGate getOneGate(Long gateId);

    DtoGate saveOneGate(@Valid DtoGateIU gate);

    DtoGate updateOneGate(Long id, DtoGateIU newGate);

    void deleteOneGate(Long gateId);

    Set<Personel> getPersonelsByGateId(Long gateId);

    ResponseEntity<String> passGate(Long wantedToEnterGate, Long personelId);


}
