package com.personneltrackingsystem.service.Impl;

import com.personneltrackingsystem.dto.DtoGate;
import com.personneltrackingsystem.dto.DtoFloor;
import com.personneltrackingsystem.entity.Floor;
import com.personneltrackingsystem.entity.Gate;
import com.personneltrackingsystem.entity.Personel;
import com.personneltrackingsystem.entity.Building;
import com.personneltrackingsystem.exception.*;
import com.personneltrackingsystem.mapper.FloorMapper;
import com.personneltrackingsystem.mapper.GateMapper;
import com.personneltrackingsystem.repository.FloorRepository;
import com.personneltrackingsystem.repository.GateRepository;
import com.personneltrackingsystem.repository.BuildingRepository;
import com.personneltrackingsystem.service.BuildingService;
import com.personneltrackingsystem.service.FloorService;
import com.personneltrackingsystem.dto.GatePassageEventDto;
// import com.personneltrackingsystem.service.KafkaProducerService;
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

    private final BuildingService buildingService;

    private final MessageResolver messageResolver;

    private final FloorMapper floorMapper;

    // private final KafkaProducerService kafkaProducerService;


    public Floor checkIfFloorExists(Long floorId){
        return floorRepository.findById(floorId)
            .orElseThrow(() -> new EntityNotFoundException("Floor not found with id: " + floorId));
    }

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
    public DtoFloor saveOneFloor(DtoFloor floor) {

        String floorName = floor.getFloorName();
        if (ObjectUtils.isEmpty(floorName)) {
            throw new BaseException(new ErrorMessage(MessageType.REQUIRED_FIELD_AVAILABLE, null));
        }


        Floor pFloor = floorMapper.dtoFloorToFloor(floor);
        
        // Find and set building if buildingId is provided
        if (floor.getBuildingId() != null) {
            Building building = buildingService.checkIfBuildingExists(floor.getBuildingId());
            pFloor.setBuilding(building);
        }

        Floor dbFloor = floorRepository.save(pFloor);

        return floorMapper.floorToDtoFloor(dbFloor);
    }

    
    @Override
    @Transactional
    public DtoFloor updateOneFloor(Long id, DtoFloor newFloor) {
        Floor existingFloor = floorRepository.findById(id)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.NO_RECORD_EXIST, messageResolver.toString())));

        if (ObjectUtils.isNotEmpty(newFloor.getFloorName())) {
            existingFloor.setFloorName(newFloor.getFloorName());
        }
        
        if (ObjectUtils.isNotEmpty(newFloor.getBuildingId())) {
            Building building = buildingService.checkIfBuildingExists(newFloor.getBuildingId());
            existingFloor.setBuilding(building);
        }

        Floor updatedFloor = floorRepository.save(existingFloor);
        return floorMapper.floorToDtoFloor(updatedFloor);
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
