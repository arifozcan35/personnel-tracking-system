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

    private final FloorService floorServiceImpl;


    @Override
    public List<DtoFloor> getAllFloors() {
        return floorServiceImpl.getAllFloors();
    }


    @Override
    public DtoFloor getOneFloor(Long floorId) {
        return floorServiceImpl.getOneFloor(floorId);
    }


    @Override
    public DtoFloor createFloor(DtoFloor newFloor) {
        return floorServiceImpl.saveOneFloor(newFloor);
    }


    @Override
    public DtoFloor updateFloor(Long floorId, DtoFloor newFloor) {
        return floorServiceImpl.updateOneFloor(floorId, newFloor);
    }


    @Override
    public void deleteFloor(Long floorId) {
        floorServiceImpl.deleteOneFloor(floorId);
    }
}
