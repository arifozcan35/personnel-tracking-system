package com.personneltrackingsystem.service.Impl;

import com.personneltrackingsystem.entity.Gate;
import com.personneltrackingsystem.entity.Personel;
import com.personneltrackingsystem.repository.GateRepository;
import com.personneltrackingsystem.repository.PersonelRepository;
import com.personneltrackingsystem.service.IGateService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GateServiceImpl implements IGateService {
    @Autowired
    private GateRepository gateRepository;

    @Autowired
    private PersonelRepository personelRepository;

    @Override
    public List<Gate> getAllGates(){

        return gateRepository.findAll();
    }


    @Override
    public Gate getOneGate(Long gateId){
        return gateRepository.findById(gateId).orElse(null);
    }

    @Override
    public Gate saveOneGate(Gate gate) {
        if(gate.getGateName() != null){
            return gateRepository.save(gate);
        }else{
            return null;
        }
    }

    @Override
    public Gate updateOneGate(Long id, Gate yeniGate) {
        Optional<Gate> gate = gateRepository.findById(id);

        if(gate.isPresent()){
            Gate foundGate = gate.get();
            foundGate.setGateName(yeniGate.getGateName());
            return gateRepository.save(foundGate);
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
