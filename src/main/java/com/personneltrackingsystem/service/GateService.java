package com.personneltrackingsystem.service;

import com.personneltrackingsystem.dto.DtoGate;
import com.personneltrackingsystem.dto.DtoGateIU;
import com.personneltrackingsystem.dto.DtoPersonel;
import com.personneltrackingsystem.dto.DtoPersonelIU;
import com.personneltrackingsystem.entity.Personel;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface GateService {

    public List<DtoGate> getAllGates();

    public DtoGate getOneGate(Long gateId);

    public DtoGate saveOneGate(DtoGateIU gate);

    public DtoGate updateOneGate(Long id, DtoGateIU newGate);

    public void deleteOneGate(Long gateId);

    public List<Personel> getPersonelsByGateId(Long gateId);

    public ResponseEntity<String> passGate(Long wantedToEnterGate, Personel personel);


}
