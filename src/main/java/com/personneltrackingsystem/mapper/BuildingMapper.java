package com.personneltrackingsystem.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.personneltrackingsystem.dto.DtoBuilding;
import com.personneltrackingsystem.entity.Building;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BuildingMapper {

    Building dtoBuildingToBuilding(DtoBuilding dtoBuilding);

    List<DtoBuilding> buildingListToDtoBuildingList(List<Building> buildingList);

    DtoBuilding buildingToDtoBuilding(Building building);

    
}
