package com.personneltrackingsystem.service;

import com.personneltrackingsystem.dto.DtoBuilding;
import com.personneltrackingsystem.entity.Building;

import java.util.List;
import java.util.Optional;

public interface BuildingService {
    
    List<DtoBuilding> getAllBuildings();

    Optional<DtoBuilding> getBuildingById(Long id);

    DtoBuilding getOneBuilding(Long id);

    DtoBuilding saveOneBuilding(DtoBuilding newBuilding);

    DtoBuilding updateOneBuilding(Long id, DtoBuilding newBuilding);
    
    void deleteOneBuilding(Long id);

    Building checkIfBuildingExists(Long buildingId);
} 