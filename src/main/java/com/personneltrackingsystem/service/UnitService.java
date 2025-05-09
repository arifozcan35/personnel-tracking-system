package com.personneltrackingsystem.service;

import com.personneltrackingsystem.dto.DtoUnit;
import com.personneltrackingsystem.dto.DtoUnitIU;

import java.util.List;
import java.util.Optional;

public interface UnitService {

    Optional<DtoUnitIU> findById(Long unitId);

    List<DtoUnit> getAllUnits();

    DtoUnit getOneUnit(Long unitId);

    DtoUnit saveOneUnit(DtoUnit unit);

    DtoUnit updateOneUnit(Long id, DtoUnitIU newUnit);

    void deleteOneUnit(Long unitId);

}
