package com.personneltrackingsystem.dto;

import com.personneltrackingsystem.entity.Building;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter; 

@Getter
@Setter
@AllArgsConstructor
public class FloorResponseDTO {

    private String floorName;

    private Building buildingId;
}
