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
public class DtoGateIU {

    @Schema(description = "The name of gate", example = "Gate 1")
    private String gateName;

    @Schema(description = "The main entrance of gate", example = "true")
    private Boolean mainEntrance;

    @Schema(description = "The unit id of gate", example = "1")
    private Long unitId;
}
