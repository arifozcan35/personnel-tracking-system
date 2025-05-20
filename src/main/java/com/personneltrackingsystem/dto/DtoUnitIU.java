package com.personneltrackingsystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DtoUnitIU {

    // solid example : article 2 (Open Closed Principle)

    @Schema(description = "The name of unit", example = "Information Technologies")
    private String birimIsim;

    @Schema(description = "The floor id of unit", example = "4")
    private Long floorId;

    @Schema(description = "The administrator personel id of unit", example = "2")
    private Long administratorPersonelId;
}
