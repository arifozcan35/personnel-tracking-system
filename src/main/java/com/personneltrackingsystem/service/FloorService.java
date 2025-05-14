package com.personneltrackingsystem.service;

import com.personneltrackingsystem.dto.DtoFloorIU;
import com.personneltrackingsystem.dto.DtoFloor;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;

@Validated
public interface FloorService {

    Optional<DtoFloor> findById(Long floorId);

    List<DtoFloor> getAllFloors();

    DtoFloor getOneFloor(Long floorId);

    DtoFloor saveOneFloor(DtoFloorIU floor);

    DtoFloor updateOneFloor(Long id, DtoFloorIU newFloor);

    void deleteOneFloor(Long floorId);


    ResponseEntity<String> passFloor(Long wantedToEnterFloor, Long personelId);

}
