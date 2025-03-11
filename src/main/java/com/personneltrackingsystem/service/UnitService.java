package com.personneltrackingsystem.service;

import com.personneltrackingsystem.dto.DtoUnit;
import com.personneltrackingsystem.dto.DtoUnitIU;
import com.personneltrackingsystem.entity.Personel;

import java.util.List;

public interface UnitService {

    public List<DtoUnit> getAllUnits();

    public DtoUnit getOneUnit(Long unitId);

    public DtoUnit saveOneUnit(DtoUnitIU unit);

    public DtoUnit updateOneUnit(Long id, DtoUnitIU newUnit);

    public void deleteOneUnit(Long unitId);

    public List<Personel> getPersonelsByUnitId(Long unitId);
}
