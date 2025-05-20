package com.personneltrackingsystem.controller.Impl;

import com.personneltrackingsystem.controller.GateController;
import com.personneltrackingsystem.dto.DtoGate;
import com.personneltrackingsystem.dto.DtoGateIU;
import com.personneltrackingsystem.service.GateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class GateControllerImpl implements GateController {

    private final GateService gateService;

    @Override
    public List<DtoGate> getAllGates() {
        return gateService.getAllGates();
    }

    @Override
    public DtoGate getOneGate(Long gateId) {
        return gateService.getOneGate(gateId);
    }

    @Override
    public DtoGate createGate(DtoGateIU newGate) {
        return gateService.saveOneGate(newGate);
    }

    @Override
    public DtoGate updateGate(Long gateId, DtoGateIU newGate) {
        return gateService.updateOneGate(gateId, newGate);
    }

    @Override
    public void deleteGate(Long gateId) {
        gateService.deleteOneGate(gateId);
    }
} 