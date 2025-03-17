package com.personneltrackingsystem.service.Impl;

import com.personneltrackingsystem.dto.DtoGate;
import com.personneltrackingsystem.dto.DtoGateIU;
import com.personneltrackingsystem.entity.Gate;
import com.personneltrackingsystem.entity.Personel;
import com.personneltrackingsystem.exception.BaseException;
import com.personneltrackingsystem.exception.ErrorMessage;
import com.personneltrackingsystem.exception.MessageResolver;
import com.personneltrackingsystem.exception.MessageType;
import com.personneltrackingsystem.repository.GateRepository;
import com.personneltrackingsystem.repository.PersonelRepository;
import com.personneltrackingsystem.service.GateService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@RequiredArgsConstructor
@Service
public class GateServiceImpl implements GateService {

    private final GateRepository gateRepository;

    private final PersonelRepository personelRepository;

    private final MessageResolver messageResolver;

    @Override
    public List<DtoGate> getAllGates(){
        List<DtoGate> dtoGateList = new ArrayList<>();

        List<Gate> gateList =  gateRepository.findAll();
        for (Gate gate : gateList) {
            DtoGate dto = new DtoGate();
            BeanUtils.copyProperties(gate, dto);
            dtoGateList.add(dto);
        }
        return dtoGateList;
    }


    @Override
    public DtoGate getOneGate(Long gateId){
        DtoGate dto = new DtoGate();
        Optional<Gate> optional =  gateRepository.findById(gateId);
        if(optional.isEmpty()){
            ErrorMessage errorMessage = new ErrorMessage(MessageType.NO_RECORD_EXIST, messageResolver.toString());
            throw new BaseException(errorMessage);
        }else{
            Gate dbGate = optional.get();

            BeanUtils.copyProperties(dbGate, dto);
            return dto;
        }
    }


    @Override
    public DtoGate saveOneGate(DtoGateIU gate) {
        if(gate.getGateName() != null){
            DtoGate dto = new DtoGate();
            Gate pGate = new Gate();
            BeanUtils.copyProperties(gate, pGate);

            Gate dbGate = gateRepository.save(pGate);
            BeanUtils.copyProperties(dbGate, dto);

            return dto;
        }else{
            ErrorMessage errorMessage = new ErrorMessage(MessageType.REQUIRED_FIELD_AVAILABLE, messageResolver.toString());
            throw new BaseException(errorMessage);
        }
    }


    @Override
    public DtoGate updateOneGate(Long id, DtoGateIU newGate) {
        DtoGate dto = new DtoGate();

        Optional<Gate> gate = gateRepository.findById(id);

        if(gate.isPresent()){
            Gate foundGate = gate.get();
            foundGate.setGateName(newGate.getGateName());

            Gate updatedGate = gateRepository.save(foundGate);

            BeanUtils.copyProperties(updatedGate, dto);

            return dto;
        }else{
            ErrorMessage errorMessage = new ErrorMessage(MessageType.NO_RECORD_EXIST, messageResolver.toString());
            throw new BaseException(errorMessage);
        }

    }


    @Override
    public void deleteOneGate(Long gateId) {
        Optional<Gate> gate = gateRepository.findById(gateId);

        if(gate.isPresent()){
            // Make the associated personnel's gate field null.
            List<Personel> personels = personelRepository.findByGate(gate.get());
            for (Personel personel : personels) {
                personel.setGate(null);
            }

            personelRepository.saveAll(personels);

            gateRepository.delete(gate.get());
        }
        else{
            ErrorMessage errorMessage = new ErrorMessage(MessageType.NO_RECORD_EXIST, messageResolver.toString());
            throw new BaseException(errorMessage);
        }

    }


    @Override
    public Set<Personel> getPersonelsByGateId(Long gateId) {
        Set<Personel> personels = new HashSet<>();

        Optional<Gate> gate = gateRepository.findById(gateId);

        if (gate.isPresent()) {
            personels.addAll(gate.get().getPersonels());
        } else {
            ErrorMessage errorMessage = new ErrorMessage(MessageType.NO_RECORD_EXIST, messageResolver.toString());
            throw new BaseException(errorMessage);
        }

        return personels;
    }


    @Override
    public ResponseEntity<String> passGate(Long wantedToEnterGate, Personel personel) {
        Long gatePersonelBelongs = personel.getGate().getGateId();

        Optional<Personel> isThisPersonelExists = personelRepository.findById(personel.getPersonelId());

        Optional<Gate> isThisValueExists = gateRepository.findById(wantedToEnterGate);

        if(isThisPersonelExists.isPresent()){
            if(isThisValueExists.isPresent()){
                if(gatePersonelBelongs.equals(wantedToEnterGate)){
                    return new ResponseEntity<>("Personnel entered the gate!", HttpStatus.CREATED);
                }else{
                    return new ResponseEntity<>("Personnel is not authorized to enter this gate!", HttpStatus.BAD_REQUEST);
                }
            }else{
                return new ResponseEntity<>("The gate you want to enter is not available!", HttpStatus.BAD_REQUEST);
            }
        }else{
            return new ResponseEntity<>("This personnel is not available! Entry from outside the institution is prohibited!", HttpStatus.BAD_REQUEST);
        }

    }

}
