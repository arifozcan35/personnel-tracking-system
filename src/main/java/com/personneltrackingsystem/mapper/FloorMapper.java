package com.personneltrackingsystem.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.personneltrackingsystem.dto.DtoBuildingIU;
import com.personneltrackingsystem.dto.DtoFloor;
import com.personneltrackingsystem.dto.DtoFloorIU;
import com.personneltrackingsystem.entity.Floor;

@Mapper(componentModel = "spring")
public interface FloorMapper {

    Floor dtoFloorToFloor(DtoFloor dtoFloor);

    List<DtoFloor> floorListToDtoFloorList(List<Floor> floorList);

    DtoFloor floorToDtoFloor(Floor floor);

    DtoFloorIU floorToDtoFloorIU(Floor floor);



    default Long map(DtoBuildingIU buildingId) {
        return buildingId.getBuildingId();
    }

}
