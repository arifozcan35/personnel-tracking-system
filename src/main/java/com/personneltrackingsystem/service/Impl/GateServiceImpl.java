package com.personneltrackingsystem.service.Impl;

import com.personneltrackingsystem.dto.DtoGate;
import com.personneltrackingsystem.dto.DtoGateIU;
import com.personneltrackingsystem.entity.Gate;
import com.personneltrackingsystem.entity.Unit;
import com.personneltrackingsystem.exception.BaseException;
import com.personneltrackingsystem.exception.ErrorMessage;
import com.personneltrackingsystem.exception.MessageResolver;
import com.personneltrackingsystem.exception.MessageType;
import com.personneltrackingsystem.exception.ValidationException;
import com.personneltrackingsystem.mapper.GateMapper;
import com.personneltrackingsystem.repository.GateRepository;
import com.personneltrackingsystem.service.GateService;
import com.personneltrackingsystem.service.UnitService;

import jakarta.persistence.EntityNotFoundException;
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

    private final MessageResolver messageResolver;

    private final GateMapper gateMapper;


    @Override
    public Gate checkIfGateExists(Long gateId){
        return gateRepository.findById(gateId)
                .orElseThrow(() -> new EntityNotFoundException("Gate not found with id: " + gateId));
    }
    
    
    @Override
    public List<DtoGate> getAllGates(){

        List<Gate> gateList =  gateRepository.findAll();

        return gateMapper.gateListToDtoGateList(gateList);
    }

    @Override
    public Optional<DtoGate> getGateById(Long gateId) {

        // Don't make the outgoing returns optional, just make them dto

        Gate gate = gateRepository.findById(gateId)
                .orElseThrow(() -> new EntityNotFoundException("Gate not found with id: " + gateId));

        return Optional.ofNullable(gateMapper.gateToDtoGate(gate));
    }

    @Override
    public DtoGate getOneGate(Long gateId){
        Optional<Gate> optGate =  gateRepository.findById(gateId);
        if(optGate.isEmpty()){
            ErrorMessage errorMessage = new ErrorMessage(MessageType.NO_RECORD_EXIST, messageResolver.toString());
            throw new BaseException(errorMessage);
        }else{
            return gateMapper.gateToDtoGate(optGate.get());
        }
    }

    @Override
    @Transactional
    public DtoGate saveOneGate(DtoGateIU gate) {

        String gateName = gate.getGateName();
        if (ObjectUtils.isEmpty(gateName)) {
            throw new BaseException(new ErrorMessage(MessageType.REQUIRED_FIELD_AVAILABLE, null));
        }

        if (gateRepository.existsByGateName(gateName)) {
            throw new ValidationException("Gate with this gate name already exists!");
        }

        // Find and set floor if floorId is provided
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
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, messageResolver.toString())));

        if (ObjectUtils.isNotEmpty(newGate.getGateName())) {
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
            ErrorMessage errorMessage = new ErrorMessage(MessageType.NO_RECORD_EXIST, messageResolver.toString());
            throw new BaseException(errorMessage);
        }
    }

} 