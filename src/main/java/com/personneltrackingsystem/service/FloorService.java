package com.personneltrackingsystem.service;

import com.personneltrackingsystem.dto.DtoFloor;
import com.personneltrackingsystem.entity.Floor;

import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;

@Validated
public interface FloorService {

    Optional<DtoFloor> findById(Long floorId);

    List<DtoFloor> getAllFloors();

    DtoFloor getOneFloor(Long floorId);

    DtoFloor saveOneFloor(DtoFloor newFloor);

    DtoFloor updateOneFloor(Long id, DtoFloor newFloor);

    void deleteOneFloor(Long floorId);

    Floor checkIfFloorExists(Long floorId);

}
