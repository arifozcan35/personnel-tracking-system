package com.personneltrackingsystem.service.Impl;

import com.personneltrackingsystem.dto.DtoGate;
import com.personneltrackingsystem.dto.DtoGateIU;
import com.personneltrackingsystem.entity.Gate;
import com.personneltrackingsystem.entity.Personel;
import com.personneltrackingsystem.repository.GateRepository;
import com.personneltrackingsystem.repository.PersonelRepository;
import com.personneltrackingsystem.service.IGateService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class GateServiceImpl implements IGateService {
    private final GateRepository gateRepository;

    private final PersonelRepository personelRepository;

    @Autowired
    public GateServiceImpl (GateRepository gateRepository, PersonelRepository personelRepository){
        this.gateRepository = gateRepository;
        this.personelRepository = personelRepository;
    }

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
        if(optional.isPresent()){
            Gate dbGate = optional.get();

            BeanUtils.copyProperties(dbGate, dto);
        }
        return dto;
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
            return null;
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
            return null;
        }

    }

    @Override
    public void deleteOneGate(Long gateId) {
        Gate gate = gateRepository.findById(gateId)
                .orElseThrow(() -> new EntityNotFoundException("No gate found!"));

        // Make the associated personnel's teller field null.
        List<Personel> personels = personelRepository.findByGate(gate);
        for (Personel personel : personels) {
            personel.setGate(null);
        }

        personelRepository.saveAll(personels);

        gateRepository.delete(gate);
    }

    @Override
    public ResponseEntity<String> passGise(Long wantedToEnterGate, Personel personel) {
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
