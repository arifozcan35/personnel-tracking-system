package com.personneltrackingsystem.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.personneltrackingsystem.dto.DtoFloor;
import com.personneltrackingsystem.dto.DtoFloorIU;
import com.personneltrackingsystem.entity.Building;
import com.personneltrackingsystem.entity.Floor;

@Mapper(componentModel = "spring")
public interface FloorMapper {

    Floor dtoFloorToFloor(DtoFloor dtoFloor);

    List<DtoFloor> floorListToDtoFloorList(List<Floor> floorList);

    DtoFloor floorToDtoFloor(Floor floor);

    @Mapping(target = "building", source = "buildingId", qualifiedByName = "longToBuilding")
    Floor dtoFloorIUToFloor(DtoFloorIU dtoFloorIU);

    @Mapping(target = "buildingId", source = "building", qualifiedByName = "buildingToLong")
    DtoFloorIU floorToDtoFloorIU(Floor floor);
    
    @Named("buildingToLong")
    default Long buildingToLong(Building building) {
        return building != null ? building.getBuildingId() : null;
    }
    
    @Named("longToBuilding")
    default Building longToBuilding(Long buildingId) {
        if (buildingId == null) {
            return null;
        }
        Building building = new Building();
        building.setBuildingId(buildingId);
        return building;
    }
}
