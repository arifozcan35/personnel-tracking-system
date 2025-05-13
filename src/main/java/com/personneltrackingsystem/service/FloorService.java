package com.personneltrackingsystem.service;

import com.personneltrackingsystem.dto.FloorCreateUpdateRequestDTO;
import com.personneltrackingsystem.dto.FloorResponseDTO;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;

@Validated
public interface FloorService {

    Optional<FloorResponseDTO> findById(Long floorId);

    List<FloorResponseDTO> getAllFloors();

    FloorResponseDTO getOneFloor(Long floorId);

    FloorResponseDTO saveOneFloor(FloorCreateUpdateRequestDTO floor);

    FloorResponseDTO updateOneFloor(Long id, FloorCreateUpdateRequestDTO newFloor);

    void deleteOneFloor(Long floorId);


    ResponseEntity<String> passFloor(Long wantedToEnterFloor, Long personelId);

}
