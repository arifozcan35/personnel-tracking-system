package com.personneltrackingsystem.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FloorCreateUpdateRequestDTO {

    private Long floorId;

    private String floorName;

    private Long buildingId;

}
