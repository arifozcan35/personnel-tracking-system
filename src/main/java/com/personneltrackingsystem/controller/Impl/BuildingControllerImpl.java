package com.personneltrackingsystem.controller.Impl;

import com.personneltrackingsystem.controller.BuildingController;
import com.personneltrackingsystem.dto.DtoBuilding;
import com.personneltrackingsystem.service.BuildingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BuildingControllerImpl implements BuildingController {

    private final BuildingService buildingService;

    @Override
    public List<DtoBuilding> getAllBuildings() {
        return buildingService.getAllBuildings();
    }

    @Override
    public DtoBuilding getOneBuilding(Long buildingId) {
        return buildingService.getOneBuilding(buildingId);  
    }

    @Override
    public DtoBuilding createBuilding(DtoBuilding newBuilding) {
        return buildingService.saveOneBuilding(newBuilding);
    }

    @Override
    public DtoBuilding updateBuilding(Long buildingId, DtoBuilding newBuilding) {
        return buildingService.updateOneBuilding(buildingId, newBuilding);
    }

    @Override
    public void deleteBuilding(Long buildingId) {
        buildingService.deleteOneBuilding(buildingId);
    }
} 