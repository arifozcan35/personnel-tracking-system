package com.personneltrackingsystem.service;

import com.personneltrackingsystem.dto.DtoUnit;
import com.personneltrackingsystem.dto.DtoUnitIU;
import com.personneltrackingsystem.entity.Unit;

import java.util.List;
import java.util.Optional;

public interface UnitService {

    Optional<DtoUnit> findById(Long unitId);

    Unit checkIfUnitExists(Long unitId);

    List<DtoUnit> getAllUnits();

    DtoUnit getOneUnit(Long unitId);

    DtoUnit saveOneUnit(DtoUnitIU unit);

    DtoUnit updateOneUnit(Long id, DtoUnitIU newUnit);

    void deleteOneUnit(Long unitId);

}
