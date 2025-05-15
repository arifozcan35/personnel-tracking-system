package com.personneltrackingsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DtoFloorIU {

    private Long floorId;

    private String floorName;

    private Long buildingId;

}
