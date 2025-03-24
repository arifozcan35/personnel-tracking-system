package com.personneltrackingsystem.service.Impl;

import com.personneltrackingsystem.dto.DtoGate;
import com.personneltrackingsystem.dto.DtoGateIU;
import com.personneltrackingsystem.entity.Gate;
import com.personneltrackingsystem.entity.Personel;
import com.personneltrackingsystem.entity.Unit;
import com.personneltrackingsystem.exception.BaseException;
import com.personneltrackingsystem.exception.ErrorMessage;
import com.personneltrackingsystem.exception.MessageResolver;
import com.personneltrackingsystem.exception.MessageType;
import com.personneltrackingsystem.mapper.GateMapper;
import com.personneltrackingsystem.repository.GateRepository;
import com.personneltrackingsystem.service.GateService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
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
    public Optional<Gate> findById(Long gateId) {
        return gateRepository.findById(gateId);
    }

    @Override
    public List<DtoGate> getAllGates(){

        List<Gate> gateList =  gateRepository.findAll();

        return gateMapper.gatesToDtoGates(gateList);
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
    public DtoGate saveOneGate(DtoGateIU gateIU) {
        if(gateIU.getGateName() != null){
            Gate pGate = gateMapper.dtoGateIUToGate(gateIU);

            Gate dbGate = gateRepository.save(pGate);

            return gateMapper.gateToDtoGate(dbGate);
        }else{
            ErrorMessage errorMessage = new ErrorMessage(MessageType.REQUIRED_FIELD_AVAILABLE, messageResolver.toString());
            throw new BaseException(errorMessage);
        }
    }


    @Override
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
            gateRepository.updatePersonelGateReferences(gateId);

            // delete gate
            gateRepository.delete(optGate.get());
        }
        else{
            ErrorMessage errorMessage = new ErrorMessage(MessageType.NO_RECORD_EXIST, messageResolver.toString());
            throw new BaseException(errorMessage);
        }
    }


    @Override
    public Set<Personel> getPersonelsByGateId(Long gateId) {
        Set<Personel> personels = new HashSet<>();

        Optional<Gate> optGate = gateRepository.findById(gateId);

        if (optGate.isPresent()) {
            personels.addAll(optGate.get().getPersonels());
        } else {
            ErrorMessage errorMessage = new ErrorMessage(MessageType.NO_RECORD_EXIST, messageResolver.toString());
            throw new BaseException(errorMessage);
        }

        return personels;
    }


    @Override
    public ResponseEntity<String> passGate(Long wantedToEnterGate, Long personelId) {

        Optional<Personel> isThisPersonelExists = gateRepository.findPrsnlById(personelId);

        Optional<Gate> isThisValueExists = gateRepository.findById(wantedToEnterGate);

        if(isThisPersonelExists.isPresent()){
            if(isThisValueExists.isPresent()){
                if(personelId.equals(wantedToEnterGate)){
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
