package com.personneltrackingsystem.controller;

import com.personneltrackingsystem.dto.DtoUnit;
import com.personneltrackingsystem.dto.DtoUnitIU;
import com.personneltrackingsystem.entity.Personel;

import java.util.List;

public interface UnitController {

    public List<DtoUnit> getAllUnits();

    public DtoUnit getOneUnit(Long unitId);

    public DtoUnit createUnit(DtoUnitIU newUnit);

    public DtoUnit updateUnit(Long unitId, DtoUnitIU newUnit);

    public void deleteUnit(Long unitId);

    public List<Personel> getPersonels(Long unitId);
}
