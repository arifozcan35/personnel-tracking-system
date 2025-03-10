package com.personneltrackingsystem.dto;

import com.personneltrackingsystem.entity.Personel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DtoUnitIU {

    // solid example : article 2 (Open Closed Principle)

    private Long unitId;

    private String unitName;

    private List<DtoPersonel> personels;
}
