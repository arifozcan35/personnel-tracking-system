package com.personneltrackingsystem.service.Impl;

import com.personneltrackingsystem.dto.DtoGate;
import com.personneltrackingsystem.dto.DtoGateIU;
import com.personneltrackingsystem.entity.Gate;
import com.personneltrackingsystem.entity.Personel;
import com.personneltrackingsystem.exception.*;
import com.personneltrackingsystem.mapper.GateMapper;
import com.personneltrackingsystem.repository.GateRepository;
import com.personneltrackingsystem.service.GateService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@RequiredArgsConstructor
@Service
public class GateServiceImpl implements GateService {

    private final GateRepository gateRepository;

    private final MessageResolver messageResolver;

    private final GateMapper gateMapper;

    @Override
    public Optional<DtoGateIU> findById(Long gateId) {

        // Don't make the outgoing returns optional, just make them dto

        Gate gate = gateRepository.findById(gateId)
                .orElseThrow(() -> new EntityNotFoundException("Gate not found with id: " + gateId));

        return Optional.ofNullable(gateMapper.gateToDtoGateIU(gate));
    }

    @Override
    public List<DtoGate> getAllGates(){

        List<Gate> gateList =  gateRepository.findAll();

        return gateMapper.gateListToDtoGateList(gateList);
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
    public DtoGate saveOneGate(DtoGate gate) {

        if (!ObjectUtils.isEmpty(gate.getGateId())) {
            if (gateRepository.existsByGateId(gate.getGateId())) {
                throw new ValidationException("Gate with this gate ID already exists!");
            }
        }

        String gateName = gate.getGateName();
        if (ObjectUtils.isEmpty(gateName)) {
            throw new BaseException(new ErrorMessage(MessageType.REQUIRED_FIELD_AVAILABLE, null));
        }

        if (gateRepository.existsByGateName(gateName)) {
            throw new ValidationException("Gate with this gate name already exists!");
        }

        Gate pGate = gateMapper.dtoGateToGate(gate);
        Gate dbGate = gateRepository.save(pGate);

        return gateMapper.gateToDtoGate(dbGate);

    }

    @Override
    @Transactional
    public DtoGate updateOneGate(Long id, DtoGateIU newGate) {

        Optional<Gate> optGate = gateRepository.findById(id);

        if(optGate.isPresent()){
            Gate foundGate = optGate.get();
            foundGate.setGateName(newGate.getGateName());

            Gate updatedGate = gateRepository.save(foundGate);

            return gateMapper.gateToDtoGate(updatedGate);
        }else{
            ErrorMessage errorMessage = new ErrorMessage(MessageType.NO_RECORD_EXIST, messageResolver.toString());
            throw new BaseException(errorMessage);
        }

    }


    @Override
    @Transactional
    public void deleteOneGate(Long gateId) {
        Optional<Gate> optGate = gateRepository.findById(gateId);

        if(optGate.isPresent()){
            // update associated personnel records
            gateRepository.updatePersonelGateReferences(optGate.get());

            // delete gate
            gateRepository.delete(optGate.get());
        }
        else{
            ErrorMessage errorMessage = new ErrorMessage(MessageType.NO_RECORD_EXIST, messageResolver.toString());
            throw new BaseException(errorMessage);
        }
    }



    @Override
    public ResponseEntity<String> passGate(Long wantedToEnterGate, Long personelId) {

        // You can simplify this a little bit more

        Personel personel = gateRepository.findPrsnlById(personelId)
                .orElseThrow(() -> new BaseException(
                        new ErrorMessage(MessageType.NO_RECORD_EXIST, "This personnel is not available! Entry from outside the institution is prohibited!")
                ));

        Gate gate = gateRepository.findById(wantedToEnterGate)
                .orElseThrow(() -> new BaseException(
                        new ErrorMessage(MessageType.NO_RECORD_EXIST, "The gate you want to enter is not available!")
                ));

        if (!personel.getGate().getGateId().equals(gate.getGateId())) {
            throw new BaseException(
                    new ErrorMessage(MessageType.GENERAL_EXCEPTION, "Personnel is not authorized to enter this gate!")
            );
        }

        return new ResponseEntity<>("Personnel entered the gate!", HttpStatus.CREATED);
    }

}
