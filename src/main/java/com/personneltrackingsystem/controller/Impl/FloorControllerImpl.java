package com.personneltrackingsystem.controller.Impl;

import com.personneltrackingsystem.dto.DtoFloor;
import com.personneltrackingsystem.controller.FloorController;
import com.personneltrackingsystem.service.FloorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class FloorControllerImpl implements FloorController {

    private final FloorService gateServiceImpl;


    @Override
    public List<DtoFloor> getAllFloors() {
        return gateServiceImpl.getAllFloors();
    }


    @Override
    public DtoFloor getOneFloor(Long floorId) {
        return gateServiceImpl.getOneFloor(floorId);
    }


    @Override
    public DtoFloor createFloor(DtoFloor newFloor) {
        return gateServiceImpl.saveOneFloor(newFloor);
    }


    @Override
    public DtoFloor updateFloor(Long floorId, DtoFloor newFloor) {
        return gateServiceImpl.updateOneFloor(floorId, newFloor);
    }


    @Override
    public void deleteFloor(Long floorId) {
        gateServiceImpl.deleteOneFloor(floorId);
    }



    /*
    @Override
    public ResponseEntity<String> passFloor(Long wantedToEnterFloor, Long personelId){
        return gateServiceImpl.passFloor(wantedToEnterFloor, personelId);
    }
    */
}
