package com.personneltrackingsystem.dto;

import java.time.LocalTime;

import com.personneltrackingsystem.entity.PersonelType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DtoWorkingHoursIU {

    private Long workingHoursId;

    private LocalTime checkInTime;

    private LocalTime checkOutTime;

    
    private PersonelType personelTypeId;
}
