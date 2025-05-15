package com.personneltrackingsystem.service.Impl;

import com.personneltrackingsystem.dto.DtoGate;
import com.personneltrackingsystem.dto.DtoGateIU;
import com.personneltrackingsystem.dto.DtoFloorIU;
import com.personneltrackingsystem.dto.DtoFloor;
import com.personneltrackingsystem.entity.Floor;
import com.personneltrackingsystem.entity.Gate;
import com.personneltrackingsystem.entity.Personel;
import com.personneltrackingsystem.exception.*;
import com.personneltrackingsystem.mapper.FloorMapper;
import com.personneltrackingsystem.mapper.GateMapper;
import com.personneltrackingsystem.repository.FloorRepository;
import com.personneltrackingsystem.repository.GateRepository;
import com.personneltrackingsystem.service.FloorService;
import com.personneltrackingsystem.dto.GatePassageEventDto;
import com.personneltrackingsystem.service.KafkaProducerService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@RequiredArgsConstructor
@Service
public class FloorServiceImpl implements FloorService {

    private final FloorRepository floorRepository;

    private final MessageResolver messageResolver;

    private final FloorMapper floorMapper;

    private final KafkaProducerService kafkaProducerService;

    @Override
    public Optional<DtoFloor> findById(Long floorId) {

        // Don't make the outgoing returns optional, just make them dto

        Floor floor = floorRepository.findById(floorId)
                .orElseThrow(() -> new EntityNotFoundException("Floor not found with id: " + floorId));

        return Optional.ofNullable(floorMapper.floorToDtoFloor(floor));
    }

    @Override
    public List<DtoFloor> getAllFloors(){

        List<Floor> floorList =  floorRepository.findAll();

        return floorMapper.floorListToDtoFloorList(floorList);
    }


    @Override
    public DtoFloor getOneFloor(Long floorId){
        Optional<Floor> optFloor =  floorRepository.findById(floorId);
        if(optFloor.isEmpty()){
            ErrorMessage errorMessage = new ErrorMessage(MessageType.NO_RECORD_EXIST, messageResolver.toString());
            throw new BaseException(errorMessage);
        }else{
            return floorMapper.floorToDtoFloor(optFloor.get());
        }
    }


    @Override
    @Transactional
    public DtoFloor saveOneFloor(DtoFloorIU floor) {

        if (!ObjectUtils.isEmpty(floor.getFloorId())) {
            if (floorRepository.existsByFloorId(floor.getFloorId())) {
                throw new ValidationException("Floor with this floor ID already exists!");
            }
        }

        String floorName = floor.getFloorName();
        if (ObjectUtils.isEmpty(floorName)) {
            throw new BaseException(new ErrorMessage(MessageType.REQUIRED_FIELD_AVAILABLE, null));
        }

        if (floorRepository.existsByFloorName(floorName)) {
            throw new ValidationException("Floor with this floor name already exists!");
        }

        Floor pFloor = floorMapper.dtoFloorIUToFloor(floor);
        Floor dbFloor = floorRepository.save(pFloor);

        return floorMapper.floorToDtoFloor(dbFloor);

    }

    @Override
    @Transactional
    public DtoFloor updateOneFloor(Long id, DtoFloorIU newFloor) {

        Optional<Floor> optFloor = floorRepository.findById(id);

        if(optFloor.isPresent()){
            Floor foundFloor = optFloor.get();
            foundFloor.setFloorName(newFloor.getFloorName());

            Floor updatedFloor = floorRepository.save(foundFloor);

            return floorMapper.floorToDtoFloor(updatedFloor);
        }else{
            ErrorMessage errorMessage = new ErrorMessage(MessageType.NO_RECORD_EXIST, messageResolver.toString());
            throw new BaseException(errorMessage);
        }

    }


    @Override
    @Transactional
    public void deleteOneFloor(Long floorId) {
        Optional<Floor> optFloor = floorRepository.findById(floorId);

        if(optFloor.isPresent()){
            floorRepository.delete(optFloor.get());
        }
        else{
            ErrorMessage errorMessage = new ErrorMessage(MessageType.NO_RECORD_EXIST, messageResolver.toString());
            throw new BaseException(errorMessage);
        }
    }



    /* 
    @Override
    public ResponseEntity<String> passFloor(Long wantedToEnterFloor, Long personelId) {

        Personel personel = floorRepository.findPrsnlById(personelId)
                .orElseThrow(() -> new BaseException(
                        new ErrorMessage(MessageType.NO_RECORD_EXIST, "This personnel is not available! Entry from outside the institution is prohibited!")
                ));

        Floor floor = floorRepository.findById(wantedToEnterFloor)
                .orElseThrow(() -> new BaseException(
                        new ErrorMessage(MessageType.NO_RECORD_EXIST, "The floor you want to enter is not available!")
                ));

        if (!personel.getFloor().getFloorId().equals(floor.getFloorId())) {
            throw new BaseException(
                    new ErrorMessage(MessageType.GENERAL_EXCEPTION, "Personnel is not authorized to enter this floor!")
            );
        }

        if (personel.getWork() == null) {
            throw new BaseException(
                    new ErrorMessage(MessageType.NO_RECORD_EXIST, "Personnel does not have work hours record!")
            );
        }

        // Create and publish gate passage event to Kafka
        GatePassageEventDto gatePassageEvent = new GatePassageEventDto(
                personel.getPersonelId(),
                personel.getName(),
                personel.getEmail(),
                floor.getFloorId(),
                floor.getFloorName(),
                LocalDateTime.now()
        );
        
        // Send to Kafka topic
        kafkaProducerService.sendGatePassageEvent(gatePassageEvent);

        return new ResponseEntity<>("Personnel entered the floor!", HttpStatus.CREATED);
    }
    */

}
