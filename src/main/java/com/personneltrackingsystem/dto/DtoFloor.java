package com.personneltrackingsystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter; 

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DtoFloor {

    @Schema(description = "The name of floor", example = "3. Floor")
    private String floorName;

    @Schema(description = "The id of building", example = "1")
    private Long buildingId;

}
