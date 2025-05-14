package com.personneltrackingsystem.dto;

import com.personneltrackingsystem.entity.Building;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DtoFloorIU {

    private Long floorId;

    private String floorName;

    private Building buildingId;

}
