package com.personneltrackingsystem.service;

import com.personneltrackingsystem.dto.DtoUnit;
import com.personneltrackingsystem.dto.DtoUnitIU;
import com.personneltrackingsystem.entity.Personel;
import com.personneltrackingsystem.entity.Unit;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UnitService {

    DtoUnit findById(Long unitId);

    List<DtoUnit> getAllUnits();

    DtoUnit getOneUnit(Long unitId);

    DtoUnit saveOneUnit(DtoUnitIU unit);

    DtoUnit updateOneUnit(Long id, DtoUnitIU newUnit);

    void deleteOneUnit(Long unitId);

    Set<Personel> getPersonelsByUnitId(Long unitId);
}
