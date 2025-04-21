package com.personneltrackingsystem.service.Impl;

import com.personneltrackingsystem.dto.DtoUnit;
import com.personneltrackingsystem.dto.DtoUnitIU;
import com.personneltrackingsystem.entity.Personel;
import com.personneltrackingsystem.entity.Unit;
import com.personneltrackingsystem.exception.BaseException;
import com.personneltrackingsystem.mapper.UnitMapper;
import com.personneltrackingsystem.repository.UnitRepository;
import com.personneltrackingsystem.service.Impl.UnitServiceImpl;
import com.personneltrackingsystem.validator.UnitValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UnitServiceImplTest {

    @Mock
    private UnitRepository unitRepository;

    @Mock
    private UnitMapper unitMapper;

    @Mock
    private UnitValidator unitValidator;

    @InjectMocks
    private UnitServiceImpl unitService;

    private Unit unit;
    private DtoUnit dtoUnit;
    private DtoUnitIU dtoUnitIU;
    private List<Unit> unitList;
    private Set<Personel> personelSet;

    @BeforeEach
    void setUp() {
        // Initialize test data
        unit = new Unit();
        unit.setUnitId(1L);
        unit.setUnitName("Test Unit");

        dtoUnit = new DtoUnit();
        dtoUnit.setUnitId(1L);
        dtoUnit.setBirimIsim("Test Unit");

        dtoUnitIU = new DtoUnitIU();
        dtoUnitIU.setBirimIsim("Test Unit IU");

        unitList = new ArrayList<>();
        unitList.add(unit);

        personelSet = new HashSet<>();
        Personel personel = new Personel();
        personel.setPersonelId(1L);
        personelSet.add(personel);
        unit.setPersonels((List<Personel>) personelSet);
    }

    @Test
    void findById_WhenUnitExists_ShouldReturnDtoUnitIU() {
        // Arrange
        when(unitRepository.findById(anyLong())).thenReturn(Optional.of(unit));
        when(unitMapper.unitToDtoUnitIU(any(Unit.class))).thenReturn(dtoUnitIU);

        // Act
        Optional<DtoUnitIU> result = unitService.findById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(dtoUnitIU, result.get());
        verify(unitRepository).findById(1L);
        verify(unitMapper).unitToDtoUnitIU(unit);
    }

    @Test
    void findById_WhenUnitDoesNotExist_ShouldThrowEntityNotFoundException() {
        // Arrange
        when(unitRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> unitService.findById(1L));
        verify(unitRepository).findById(1L);
        verify(unitMapper, never()).unitToDtoUnitIU(any(Unit.class));
    }

    @Test
    void getAllUnits_ShouldReturnAllUnits() {
        // Arrange
        when(unitRepository.findAll()).thenReturn(unitList);
        when(unitMapper.unitToDtoUnit(any(Unit.class))).thenReturn(dtoUnit);

        // Act
        List<DtoUnit> result = unitService.getAllUnits();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(dtoUnit, result.get(0));
        verify(unitRepository).findAll();
        verify(unitMapper).unitToDtoUnit(unit);
    }

    @Test
    void getOneUnit_WhenUnitExists_ShouldReturnUnit() {
        // Arrange
        when(unitRepository.findById(anyLong())).thenReturn(Optional.of(unit));
        when(unitMapper.unitToDtoUnit(any(Unit.class))).thenReturn(dtoUnit);

        // Act
        DtoUnit result = unitService.getOneUnit(1L);

        // Assert
        assertNotNull(result);
        assertEquals(dtoUnit, result);
        verify(unitRepository).findById(1L);
        verify(unitMapper).unitToDtoUnit(unit);
    }

    @Test
    void getOneUnit_WhenUnitDoesNotExist_ShouldThrowBaseException() {
        // Arrange
        when(unitRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BaseException.class, () -> unitService.getOneUnit(1L));
        verify(unitRepository).findById(1L);
        verify(unitMapper, never()).unitToDtoUnit(any(Unit.class));
    }

    @Test
    void saveOneUnit_ShouldSaveAndReturnUnit() {
        // Arrange
        when(unitMapper.dtoUnitToUnit(any(DtoUnit.class))).thenReturn(unit);
        when(unitRepository.save(any(Unit.class))).thenReturn(unit);
        when(unitMapper.unitToDtoUnit(any(Unit.class))).thenReturn(dtoUnit);

        // Act
        DtoUnit result = unitService.saveOneUnit(dtoUnit);

        // Assert
        assertNotNull(result);
        assertEquals(dtoUnit, result);
        verify(unitValidator).checkIfUnitAlreadyExists(dtoUnit);
        verify(unitMapper).dtoUnitToUnit(dtoUnit);
        verify(unitRepository).save(unit);
        verify(unitMapper).unitToDtoUnit(unit);
    }

    @Test
    void updateOneUnit_WhenUnitExists_ShouldUpdateAndReturnUnit() {
        // Arrange
        when(unitRepository.findById(anyLong())).thenReturn(Optional.of(unit));
        when(unitRepository.save(any(Unit.class))).thenReturn(unit);
        when(unitMapper.unitToDtoUnit(any(Unit.class))).thenReturn(dtoUnit);

        // Act
        DtoUnit result = unitService.updateOneUnit(1L, dtoUnitIU);

        // Assert
        assertNotNull(result);
        assertEquals(dtoUnit, result);
        verify(unitRepository).findById(1L);
        verify(unitRepository).save(unit);
        verify(unitMapper).unitToDtoUnit(unit);
        assertEquals(dtoUnitIU.getBirimIsim(), unit.getUnitName());
    }

    @Test
    void updateOneUnit_WhenUnitDoesNotExist_ShouldThrowBaseException() {
        // Arrange
        when(unitRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BaseException.class, () -> unitService.updateOneUnit(1L, dtoUnitIU));
        verify(unitRepository).findById(1L);
        verify(unitRepository, never()).save(any(Unit.class));
        verify(unitMapper, never()).unitToDtoUnit(any(Unit.class));
    }

    @Test
    void deleteOneUnit_WhenUnitExists_ShouldDeleteUnit() {
        // Arrange
        when(unitRepository.findById(anyLong())).thenReturn(Optional.of(unit));
        doNothing().when(unitRepository).detachPersonelFromUnit(any(Unit.class));
        doNothing().when(unitRepository).delete(any(Unit.class));

        // Act
        unitService.deleteOneUnit(1L);

        // Assert
        verify(unitRepository).findById(1L);
        verify(unitRepository).detachPersonelFromUnit(unit);
        verify(unitRepository).delete(unit);
    }

    @Test
    void deleteOneUnit_WhenUnitDoesNotExist_ShouldThrowBaseException() {
        // Arrange
        when(unitRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BaseException.class, () -> unitService.deleteOneUnit(1L));
        verify(unitRepository).findById(1L);
        verify(unitRepository, never()).detachPersonelFromUnit(any(Unit.class));
        verify(unitRepository, never()).delete(any(Unit.class));
    }

    @Test
    void getPersonelsByUnitId_WhenUnitExists_ShouldReturnPersonels() {
        // Arrange
        when(unitRepository.findById(anyLong())).thenReturn(Optional.of(unit));

        // Act
        Set<Personel> result = unitService.getPersonelsByUnitId(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(unitRepository).findById(1L);
    }

    @Test
    void getPersonelsByUnitId_WhenUnitDoesNotExist_ShouldThrowBaseException() {
        // Arrange
        when(unitRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BaseException.class, () -> unitService.getPersonelsByUnitId(1L));
        verify(unitRepository).findById(1L);
    }
}