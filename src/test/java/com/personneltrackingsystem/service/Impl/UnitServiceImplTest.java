package com.personneltrackingsystem.service.Impl;

import com.personneltrackingsystem.dto.DtoUnit;
import com.personneltrackingsystem.dto.DtoUnitIU;
import com.personneltrackingsystem.entity.Personel;
import com.personneltrackingsystem.entity.Unit;
import com.personneltrackingsystem.exception.BaseException;
import com.personneltrackingsystem.mapper.UnitMapper;
import com.personneltrackingsystem.repository.UnitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UnitServiceImplTest {

    @Mock
    private UnitRepository unitRepository;

    @Mock
    private UnitMapper unitMapper;

    @InjectMocks
    private UnitServiceImpl unitService;

    private Unit mockUnit;
    private DtoUnit mockDtoUnit;
    private DtoUnitIU mockDtoUnitIU;

    @BeforeEach
    void setUp() {
        mockUnit = new Unit();
        mockUnit.setUnitId(1L);
        mockUnit.setUnitName("Test Unit");

        mockDtoUnit = new DtoUnit();
        mockDtoUnit.setUnitId(1L);
        mockDtoUnit.setBirimIsim("Test Unit");

        mockDtoUnitIU = new DtoUnitIU();
        mockDtoUnitIU.setBirimIsim("Test Unit");
    }

    @Test
    void testFindById_Exists() {
        when(unitRepository.findById(1L)).thenReturn(Optional.of(mockUnit));

        Optional<Unit> result = unitService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(mockUnit, result.get());
        verify(unitRepository).findById(1L);
    }

    @Test
    void testGetAllUnits() {
        List<Unit> units = Arrays.asList(mockUnit);
        List<DtoUnit> dtoUnits = Arrays.asList(mockDtoUnit);

        when(unitRepository.findAll()).thenReturn(units);
        when(unitMapper.unitsToDtoUnits(units)).thenReturn(dtoUnits);

        List<DtoUnit> result = unitService.getAllUnits();

        assertEquals(dtoUnits, result);
        verify(unitRepository).findAll();
        verify(unitMapper).unitsToDtoUnits(units);
    }

    @Test
    void testGetOneUnit_Exists() {
        when(unitRepository.findById(1L)).thenReturn(Optional.of(mockUnit));
        when(unitMapper.unitToDtoUnit(mockUnit)).thenReturn(mockDtoUnit);

        DtoUnit result = unitService.getOneUnit(1L);

        assertEquals(mockDtoUnit, result);
        verify(unitRepository).findById(1L);
        verify(unitMapper).unitToDtoUnit(mockUnit);
    }

    @Test
    void testGetOneUnit_NotExists() {
        when(unitRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(BaseException.class, () -> unitService.getOneUnit(1L));
        verify(unitRepository).findById(1L);
    }

    @Test
    void testSaveOneUnit_Success() {
        when(unitMapper.dtoUnitIUToUnit(mockDtoUnitIU)).thenReturn(mockUnit);
        when(unitRepository.save(mockUnit)).thenReturn(mockUnit);
        when(unitMapper.unitToDtoUnit(mockUnit)).thenReturn(mockDtoUnit);

        DtoUnit result = unitService.saveOneUnit(mockDtoUnitIU);

        assertEquals(mockDtoUnit, result);
        verify(unitRepository).save(mockUnit);
    }

    @Test
    void testSaveOneUnit_MissingName() {
        mockDtoUnitIU.setBirimIsim(null);

        assertThrows(BaseException.class, () -> unitService.saveOneUnit(mockDtoUnitIU));
    }

    @Test
    void testUpdateOneUnit_Success() {
        Unit existingUnit = new Unit();
        existingUnit.setUnitId(1L);
        existingUnit.setUnitName("Old Unit Name");

        when(unitRepository.findById(1L)).thenReturn(Optional.of(existingUnit));
        when(unitRepository.save(existingUnit)).thenReturn(existingUnit);
        when(unitMapper.unitToDtoUnit(existingUnit)).thenReturn(mockDtoUnit);

        DtoUnit result = unitService.updateOneUnit(1L, mockDtoUnitIU);

        assertEquals(mockDtoUnit, result);
        assertEquals("Test Unit", existingUnit.getUnitName());
        verify(unitRepository).findById(1L);
        verify(unitRepository).save(existingUnit);
    }

    @Test
    void testUpdateOneUnit_NotExists() {
        when(unitRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(BaseException.class, () -> unitService.updateOneUnit(1L, mockDtoUnitIU));
        verify(unitRepository).findById(1L);
    }

    @Test
    void testDeleteOneUnit_Success() {
        when(unitRepository.findById(1L)).thenReturn(Optional.of(mockUnit));

        unitService.deleteOneUnit(1L);

        verify(unitRepository).detachPersonelFromUnit(mockUnit);
        verify(unitRepository).delete(mockUnit);
    }

    @Test
    void testDeleteOneUnit_NotExists() {
        when(unitRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(BaseException.class, () -> unitService.deleteOneUnit(1L));
        verify(unitRepository).findById(1L);
    }

    @Test
    void testGetPersonelsByUnitId_Exists() {
        List<Personel> mockPersonels = new ArrayList<>();
        Personel personel1 = new Personel();
        personel1.setPersonelId(1L);
        mockPersonels.add(personel1);

        mockUnit.setPersonels(mockPersonels);

        when(unitRepository.findById(1L)).thenReturn(Optional.of(mockUnit));

        Set<Personel> result = unitService.getPersonelsByUnitId(1L);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(unitRepository).findById(1L);
    }

    @Test
    void testGetPersonelsByUnitId_NotExists() {
        when(unitRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(BaseException.class, () -> unitService.getPersonelsByUnitId(1L));
        verify(unitRepository).findById(1L);
    }
}