package com.personneltrackingsystem.controller;

import com.personneltrackingsystem.dto.DtoUnit;
import com.personneltrackingsystem.dto.DtoUnitIU;
import com.personneltrackingsystem.entity.Personel;

import java.util.List;
import java.util.Set;

public interface UnitController {

    List<DtoUnit> getAllUnits();

    DtoUnit getOneUnit(Long unitId);

    DtoUnit createUnit(DtoUnitIU newUnit);

    DtoUnit updateUnit(Long unitId, DtoUnitIU newUnit);

    void deleteUnit(Long unitId);

    Set<Personel> getPersonels(Long unitId);
}
