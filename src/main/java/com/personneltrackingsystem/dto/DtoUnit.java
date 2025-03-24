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
public class DtoUnit {

    @Schema(description = "Unique identity of unit", example = "3")
    private Long unitId;

    @Schema(description = "The name of unit", example = "Information Technologies")
    private String unitName;
}
