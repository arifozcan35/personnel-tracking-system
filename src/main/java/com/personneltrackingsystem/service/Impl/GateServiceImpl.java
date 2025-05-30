package com.personneltrackingsystem.service.impl;

import com.personneltrackingsystem.dto.DtoGate;
import com.personneltrackingsystem.dto.DtoGateIU;
import com.personneltrackingsystem.entity.Gate;
import com.personneltrackingsystem.entity.Unit;
import com.personneltrackingsystem.exception.BaseException;
import com.personneltrackingsystem.exception.ErrorMessage;
import com.personneltrackingsystem.exception.MessageType;
import com.personneltrackingsystem.exception.ValidationException;
import com.personneltrackingsystem.mapper.GateMapper;
import com.personneltrackingsystem.repository.GateRepository;
import com.personneltrackingsystem.service.GateService;
import com.personneltrackingsystem.service.UnitService;

import lombok.RequiredArgsConstructor;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GateServiceImpl implements GateService {

    private final GateRepository gateRepository;

    private final UnitService unitService;

    private final GateMapper gateMapper;


    @Override
    public Gate checkIfGateExists(Long gateId){
        return gateRepository.findById(gateId)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.GATE_NOT_FOUND, gateId.toString())));
    }
    
    
    @Override
    public List<DtoGate> getAllGates(){

        List<Gate> gateList =  gateRepository.findAll();

        return gateMapper.gateListToDtoGateList(gateList);
    }


    @Override
    public Optional<DtoGate> getGateById(Long gateId) {

        Gate gate = gateRepository.findById(gateId)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.GATE_NOT_FOUND, gateId.toString())));

        return Optional.ofNullable(gateMapper.gateToDtoGate(gate));
    }


    @Override
    public DtoGate getOneGate(Long gateId){
        Optional<Gate> optGate =  gateRepository.findById(gateId);
        if(optGate.isEmpty()){
            throw new BaseException(new ErrorMessage(MessageType.GATE_NOT_FOUND, gateId.toString()));
        }else{
            return gateMapper.gateToDtoGate(optGate.get());
        }
    }


    @Override
    @Transactional
    public DtoGate saveOneGate(DtoGateIU gate) {

        String gateName = gate.getGateName();
        if (ObjectUtils.isEmpty(gateName)) {
            throw new ValidationException(MessageType.GATE_NAME_REQUIRED);
        }

        if (gateRepository.existsByGateName(gateName)) {
            throw new ValidationException(MessageType.GATE_NAME_ALREADY_EXISTS, gateName);
        }

        // Find and set unit if unitId is provided
        if (ObjectUtils.isNotEmpty(gate.getUnitId())) {
            Unit unit = unitService.checkIfUnitExists(gate.getUnitId());
            gate.setUnitId(unit.getUnitId());
        }

        if(ObjectUtils.isNotEmpty(gate.getMainEntrance())){
            gate.setMainEntrance(gate.getMainEntrance());
        }

        Gate pGate = gateMapper.dtoGateIUToGate(gate);
        Gate dbGate = gateRepository.save(pGate);

        return gateMapper.gateToDtoGate(dbGate);

    }


    @Override
    @Transactional
    public DtoGate updateOneGate(Long id, DtoGateIU newGate) {
        Gate existingGate = gateRepository.findById(id)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.GATE_NOT_FOUND, id.toString())));

        if (ObjectUtils.isNotEmpty(newGate.getGateName())) {
            // Check uniqueness if the name is being changed
            if (!existingGate.getGateName().equals(newGate.getGateName()) && 
                gateRepository.existsByGateName(newGate.getGateName())) {
                throw new ValidationException(MessageType.GATE_NAME_ALREADY_EXISTS, newGate.getGateName());
            }
            existingGate.setGateName(newGate.getGateName());
        }

        if (ObjectUtils.isNotEmpty(newGate.getUnitId())) {    
            Unit unit = unitService.checkIfUnitExists(newGate.getUnitId());
            existingGate.setUnitId(unit);
        }

        if (ObjectUtils.isNotEmpty(newGate.getMainEntrance())) {
            existingGate.setMainEntrance(newGate.getMainEntrance());
        }

        Gate updatedGate = gateRepository.save(existingGate);
        return gateMapper.gateToDtoGate(updatedGate);
    }

    
    @Override
    @Transactional
    public void deleteOneGate(Long gateId) {
        Optional<Gate> optGate = gateRepository.findById(gateId);

        if(optGate.isPresent()){
            gateRepository.delete(optGate.get());
        }
        else{
            throw new BaseException(new ErrorMessage(MessageType.GATE_NOT_FOUND, gateId.toString()));
        }
    }

} 