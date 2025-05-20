package com.personneltrackingsystem.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DtoPersonelAll {

    private Long personelId;

    private String name;

    private String email;

    private DtoPersonelType personelType;

    private List<DtoUnitIU> units;
    
}
