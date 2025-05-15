package com.personneltrackingsystem.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DtoPersonelIU {


    @Schema(description = "The name of Personnel", example = "Arif Ozcan")
    private String name;

    @Schema(description = "The e-mail of Personnel", example = "zcanarif@gmail.com")
    private String email;


    private List<Long> unitId;

    private Long personelTypeId;
}
