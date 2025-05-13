package com.personneltrackingsystem.controller.Impl;

import com.personneltrackingsystem.dto.FloorCreateUpdateRequestDTO;
import com.personneltrackingsystem.dto.FloorResponseDTO;
import com.personneltrackingsystem.controller.FloorController;
import com.personneltrackingsystem.service.FloorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class FloorControllerImpl implements FloorController {

    private final FloorService gateServiceImpl;


    @Override
    public List<FloorResponseDTO> getAllFloors() {
        return gateServiceImpl.getAllFloors();
    }


    @Override
    public FloorResponseDTO getOneFloor(Long floorId) {
        return gateServiceImpl.getOneFloor(floorId);
    }


    @Override
    public FloorResponseDTO createFloor(FloorCreateUpdateRequestDTO newFloor) {
        return gateServiceImpl.saveOneFloor(newFloor);
    }


    @Override
    public FloorResponseDTO updateFloor(Long floorId, FloorCreateUpdateRequestDTO newFloor) {
        return gateServiceImpl.updateOneFloor(floorId, newFloor);
    }


    @Override
    public void deleteFloor(Long floorId) {
        gateServiceImpl.deleteOneFloor(floorId);
    }



    @Override
    public ResponseEntity<String> passFloor(Long wantedToEnterFloor, Long personelId){
        return gateServiceImpl.passFloor(wantedToEnterFloor, personelId);
    }
}
