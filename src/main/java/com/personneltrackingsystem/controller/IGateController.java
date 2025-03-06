package com.personneltrackingsystem.controller;

import com.personneltrackingsystem.entity.Gate;
import com.personneltrackingsystem.entity.Personel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface IGateController {

    public List<Gate> getAllGates();

    public Gate getOneGate(Long gateId);

    public Gate createGate(Gate newGate);

    public Gate updateGate(Long gateId, Gate newGate);

    public void deleteGate(Long gateId);

    public ResponseEntity<String> passGise(Long wantedToEnterGate, Personel personel);
}
