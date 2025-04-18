package com.personneltrackingsystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DtoGateIU {

    @Schema(description = "Unique identity of gate", example = "2")
    private Long gateId;

    @Schema(description = "The name of gate", example = "3. Floor")
    private String gateName;

    private List<DtoPersonel> personels;
}
