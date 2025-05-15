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
public class DtoPersonelType {

    @Schema(description = "The name of personel type", example = "Personnel")
    private String personelTypeName;  
}
