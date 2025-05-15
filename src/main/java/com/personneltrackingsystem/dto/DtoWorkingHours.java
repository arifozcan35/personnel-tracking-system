package com.personneltrackingsystem.dto;

import java.time.LocalTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DtoWorkingHours {

    @Schema(description = "Entry time for work", example = "09:18")
    private LocalTime checkInTime;

    @Schema(description = "Exit time for work", example = "18:00")
    private LocalTime checkOutTime;

    @Schema(description = "Personel type id", example = "1")
    private Long personelTypeId;
}
