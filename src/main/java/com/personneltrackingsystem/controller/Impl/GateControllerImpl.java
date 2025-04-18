package com.personneltrackingsystem.controller.Impl;

import com.personneltrackingsystem.dto.DtoGate;
import com.personneltrackingsystem.dto.DtoGateIU;
import com.personneltrackingsystem.controller.GateController;
import com.personneltrackingsystem.entity.Personel;
import com.personneltrackingsystem.service.GateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
public class GateControllerImpl implements GateController {

    private final GateService gateServiceImpl;


    @Override
    public List<DtoGate> getAllGates() {
        return gateServiceImpl.getAllGates();
    }


    @Override
    public DtoGate getOneGate(Long gateId) {
        return gateServiceImpl.getOneGate(gateId);
    }


    @Override
    public DtoGate createGate(DtoGateIU newGate) {
        return gateServiceImpl.saveOneGate(newGate);
    }


    @Override
    public DtoGate updateGate(Long gateId, DtoGateIU newGate) {
        return gateServiceImpl.updateOneGate(gateId, newGate);
    }


    @Override
    public void deleteGate(Long gateId) {
        gateServiceImpl.deleteOneGate(gateId);
    }



    @Override
    public Set<Personel> getPersonels(Long gateId) {
        return gateServiceImpl.getPersonelsByGateId(gateId);
    }


    @Override
    public ResponseEntity<String> passGate(Long wantedToEnterGate, Long personelId){
        return gateServiceImpl.passGate(wantedToEnterGate, personelId);
    }
}
