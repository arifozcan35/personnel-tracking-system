package com.personneltrackingsystem.service.impl;

import com.personneltrackingsystem.dto.DtoFloor;
import com.personneltrackingsystem.entity.Floor;
import com.personneltrackingsystem.entity.Building;
import com.personneltrackingsystem.exception.*;
import com.personneltrackingsystem.mapper.FloorMapper;
import com.personneltrackingsystem.repository.FloorRepository;
import com.personneltrackingsystem.service.BuildingService;
import com.personneltrackingsystem.service.FloorService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@RequiredArgsConstructor
@Service
public class FloorServiceImpl implements FloorService {

    private final FloorRepository floorRepository;

    private final BuildingService buildingService;

    private final FloorMapper floorMapper;


    @Override
    public Floor checkIfFloorExists(Long floorId){
        return floorRepository.findById(floorId)
            .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.FLOOR_NOT_FOUND, floorId.toString())));
    }


    @Override
    public Optional<DtoFloor> findById(Long floorId) {

        Floor floor = floorRepository.findById(floorId)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.FLOOR_NOT_FOUND, floorId.toString())));

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
            throw new BaseException(new ErrorMessage(MessageType.FLOOR_NOT_FOUND, floorId.toString()));
        }else{
            return floorMapper.floorToDtoFloor(optFloor.get());
        }
    }


    @Override
    @Transactional
    public DtoFloor saveOneFloor(DtoFloor floor) {

        String floorName = floor.getFloorName();
        if (ObjectUtils.isEmpty(floorName)) {
            throw new ValidationException(MessageType.FLOOR_NAME_REQUIRED);
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
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessageType.FLOOR_NOT_FOUND, id.toString())));

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
            throw new BaseException(new ErrorMessage(MessageType.FLOOR_NOT_FOUND, floorId.toString()));
        }
    }
}
