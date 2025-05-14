package com.personneltrackingsystem.service;

import com.personneltrackingsystem.dto.DtoBuilding;
import com.personneltrackingsystem.dto.DtoBuildingIU;

import java.util.List;
import java.util.Optional;

public interface BuildingService {
    List<DtoBuilding> getAllBuildings();
    Optional<DtoBuilding> getBuildingById(Long id);
    DtoBuilding getOneBuilding(Long id);
    DtoBuilding saveOneBuilding(DtoBuildingIU buildingDto);
    DtoBuilding updateOneBuilding(Long id, DtoBuildingIU buildingDto);
    void deleteOneBuilding(Long id);
} 