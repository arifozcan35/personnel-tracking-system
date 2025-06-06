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
public class DtoPersonelCache {

    private Long personelId;

    private String name;

    private String email;

    private Long personelTypeId;

    private String personelTypeName;
    
    private List<DtoUnitCache> units;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DtoUnitCache {
        private Long unitId;
        private String unitName;
    }
} 