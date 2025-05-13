package com.personneltrackingsystem.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.personneltrackingsystem.dto.FloorCreateUpdateRequestDTO;
import com.personneltrackingsystem.dto.FloorResponseDTO;
import com.personneltrackingsystem.entity.Floor;
import com.personneltrackingsystem.entity.Building;

@Mapper(componentModel = "spring")
public interface FloorMapper {

    @Mapping(target = "buildingId", source = "buildingId", qualifiedByName = "buildingIdToBuilding")
    Floor floorCreateUpdateRequestDTOToFloor(FloorCreateUpdateRequestDTO floorCreateUpdateRequestDTO);
    
    Floor floorResponseDTOToFloor(FloorResponseDTO floorResponseDTO);   

    @Mapping(target = "buildingId", source = "buildingId.buildingId")
    FloorCreateUpdateRequestDTO floorToFloorCreateUpdateRequestDTO(Floor floor);

    FloorResponseDTO floorToFloorResponseDTO(Floor floor);

    List<FloorResponseDTO> floorListToFloorResponseDTOList(List<Floor> floorList);

    @Named("buildingIdToBuilding")
    default Building buildingIdToBuilding(Long buildingId) {
        if (buildingId == null) {
            return null;
        }
        Building building = new Building();
        building.setBuildingId(buildingId);
        return building;
    }
}
