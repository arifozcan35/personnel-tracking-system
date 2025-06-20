package com.personneltrackingsystem.service.impl;

import com.personneltrackingsystem.dto.DtoUnit;
import com.personneltrackingsystem.dto.DtoUnitIU;
import com.personneltrackingsystem.entity.Floor;
import com.personneltrackingsystem.entity.Personel;
import com.personneltrackingsystem.entity.Unit;
import com.personneltrackingsystem.exception.BaseException;
import com.personneltrackingsystem.exception.ValidationException;
import com.personneltrackingsystem.mapper.UnitMapper;
import com.personneltrackingsystem.repository.UnitRepository;
import com.personneltrackingsystem.service.FloorService;
import com.personneltrackingsystem.service.PersonelService;
import com.personneltrackingsystem.service.impl.UnitServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UnitServiceImplTest {

    @Mock
    private UnitRepository unitRepository;

    @Mock
    private FloorService floorService;

    @Mock
    private PersonelService personelService;

    @Mock
    private UnitMapper unitMapper;

    @InjectMocks
    private UnitServiceImpl unitService;

    private Unit unit;
    private DtoUnit dtoUnit;
    private DtoUnitIU dtoUnitIU;
    private Floor floor;
    private Personel personel;

    @BeforeEach
    void setUp() {
        unit = new Unit();
        unit.setUnitId(1L);
        unit.setUnitName("Test Birim");

        floor = new Floor();
        floor.setFloorId(1L);

        personel = new Personel();
        personel.setPersonelId(1L);

        unit.setFloorId(floor);
        unit.setAdministratorPersonelId(personel);

        dtoUnit = new DtoUnit();
        dtoUnit.setBirimIsim("Test Birim");
        dtoUnit.setAdministratorPersonelId(1L);

        dtoUnitIU = new DtoUnitIU();
        dtoUnitIU.setBirimIsim("Test Birim");
        dtoUnitIU.setFloorId(1L);
        dtoUnitIU.setAdministratorPersonelId(1L);
    }

    @Test
    void findById_WhenUnitExists_ReturnsDtoUnit() {
        // Given
        when(unitRepository.findById(anyLong())).thenReturn(Optional.of(unit));
        when(unitMapper.unitToDtoUnit(any(Unit.class))).thenReturn(dtoUnit);

        // When
        Optional<DtoUnit> result = unitService.findById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals("Test Birim", result.get().getBirimIsim());
        verify(unitRepository).findById(1L);
        verify(unitMapper).unitToDtoUnit(unit);
    }

    @Test
    void findById_WhenUnitDoesNotExist_ThrowsBaseException() {
        // Given
        when(unitRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When/Then
        assertThrows(BaseException.class, () -> unitService.findById(1L));
        verify(unitRepository).findById(1L);
        verify(unitMapper, never()).unitToDtoUnit(any(Unit.class));
    }

    @Test
    void checkIfUnitExists_WhenUnitExists_ReturnsUnit() {
        // Given
        when(unitRepository.findById(anyLong())).thenReturn(Optional.of(unit));

        // When
        Unit result = unitService.checkIfUnitExists(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getUnitId());
        verify(unitRepository).findById(1L);
    }

    @Test
    void checkIfUnitExists_WhenUnitDoesNotExist_ThrowsBaseException() {
        // Given
        when(unitRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When/Then
        assertThrows(BaseException.class, () -> unitService.checkIfUnitExists(1L));
        verify(unitRepository).findById(1L);
    }

    @Test
    void getAllUnits_ReturnsAllUnits() {
        // Given
        List<Unit> units = Arrays.asList(unit);
        List<DtoUnit> dtoUnits = Arrays.asList(dtoUnit);
        
        when(unitRepository.findAll()).thenReturn(units);
        when(unitMapper.unitListToDtoUnitList(units)).thenReturn(dtoUnits);

        // When
        List<DtoUnit> result = unitService.getAllUnits();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Birim", result.get(0).getBirimIsim());
        verify(unitRepository).findAll();
        verify(unitMapper).unitListToDtoUnitList(units);
    }

    @Test
    void getOneUnit_WhenUnitExists_ReturnsDtoUnit() {
        // Given
        when(unitRepository.findById(anyLong())).thenReturn(Optional.of(unit));
        when(unitMapper.unitToDtoUnit(any(Unit.class))).thenReturn(dtoUnit);

        // When
        DtoUnit result = unitService.getOneUnit(1L);

        // Then
        assertNotNull(result);
        assertEquals("Test Birim", result.getBirimIsim());
        verify(unitRepository).findById(1L);
        verify(unitMapper).unitToDtoUnit(unit);
    }

    @Test
    void getOneUnit_WhenUnitDoesNotExist_ThrowsBaseException() {
        // Given
        when(unitRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When/Then
        assertThrows(BaseException.class, () -> unitService.getOneUnit(1L));
        verify(unitRepository).findById(1L);
        verify(unitMapper, never()).unitToDtoUnit(any(Unit.class));
    }

    @Test
    void saveOneUnit_WhenUnitNameIsEmpty_ThrowsValidationException() {
        // Given
        dtoUnitIU.setBirimIsim(null);

        // When/Then
        assertThrows(ValidationException.class, () -> unitService.saveOneUnit(dtoUnitIU));
        verify(unitRepository, never()).save(any(Unit.class));
    }

    @Test
    void saveOneUnit_WhenUnitNameAlreadyExists_ThrowsValidationException() {
        // Given
        when(unitRepository.existsByUnitName(anyString())).thenReturn(true);

        // When/Then
        assertThrows(ValidationException.class, () -> unitService.saveOneUnit(dtoUnitIU));
        verify(unitRepository).existsByUnitName(dtoUnitIU.getBirimIsim());
        verify(unitRepository, never()).save(any(Unit.class));
    }

    @Test
    void saveOneUnit_WhenFloorIdIsNotEmpty_SetsFloor() {
        // Given
        when(unitRepository.existsByUnitName(anyString())).thenReturn(false);
        when(floorService.checkIfFloorExists(anyLong())).thenReturn(floor);
        when(personelService.checkIfPersonelExists(anyLong())).thenReturn(personel);
        when(unitMapper.dtoUnitIUToUnit(any(DtoUnitIU.class))).thenReturn(unit);
        when(unitRepository.save(any(Unit.class))).thenReturn(unit);
        when(unitMapper.unitToDtoUnit(any(Unit.class))).thenReturn(dtoUnit);

        // When
        DtoUnit result = unitService.saveOneUnit(dtoUnitIU);

        // Then
        assertNotNull(result);
        verify(floorService).checkIfFloorExists(dtoUnitIU.getFloorId());
        verify(personelService).checkIfPersonelExists(dtoUnitIU.getAdministratorPersonelId());
        verify(unitMapper).dtoUnitIUToUnit(dtoUnitIU);
        verify(unitRepository).save(unit);
        verify(unitMapper).unitToDtoUnit(unit);
    }

    @Test
    void updateOneUnit_WhenUnitDoesNotExist_ThrowsBaseException() {
        // Given
        when(unitRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When/Then
        assertThrows(BaseException.class, () -> unitService.updateOneUnit(1L, dtoUnitIU));
        verify(unitRepository).findById(1L);
        verify(unitRepository, never()).save(any(Unit.class));
    }

    @Test
    void updateOneUnit_WhenUnitNameIsChanged_UpdatesName() {
        // Given
        Unit existingUnit = new Unit();
        existingUnit.setUnitId(1L);
        existingUnit.setUnitName("Eski Birim");

        dtoUnitIU.setBirimIsim("Yeni Birim");

        when(unitRepository.findById(anyLong())).thenReturn(Optional.of(existingUnit));
        when(unitRepository.existsByUnitName(anyString())).thenReturn(false);
        when(floorService.checkIfFloorExists(anyLong())).thenReturn(floor);
        when(personelService.checkIfPersonelExists(anyLong())).thenReturn(personel);
        when(unitRepository.save(any(Unit.class))).thenReturn(existingUnit);
        when(unitMapper.unitToDtoUnit(any(Unit.class))).thenReturn(dtoUnit);

        // When
        DtoUnit result = unitService.updateOneUnit(1L, dtoUnitIU);

        // Then
        assertNotNull(result);
        verify(unitRepository).findById(1L);
        verify(unitRepository).existsByUnitName("Yeni Birim");
        verify(floorService).checkIfFloorExists(dtoUnitIU.getFloorId());
        verify(personelService).checkIfPersonelExists(dtoUnitIU.getAdministratorPersonelId());
        verify(unitRepository).save(existingUnit);
        assertEquals("Yeni Birim", existingUnit.getUnitName());
    }

    @Test
    void deleteOneUnit_WhenUnitExists_DeletesUnit() {
        // Given
        when(unitRepository.findById(anyLong())).thenReturn(Optional.of(unit));

        // When
        unitService.deleteOneUnit(1L);

        // Then
        verify(unitRepository).findById(1L);
        verify(unitRepository).delete(unit);
    }

    @Test
    void deleteOneUnit_WhenUnitDoesNotExist_ThrowsBaseException() {
        // Given
        when(unitRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When/Then
        assertThrows(BaseException.class, () -> unitService.deleteOneUnit(1L));
        verify(unitRepository).findById(1L);
        verify(unitRepository, never()).delete(any(Unit.class));
    }
} 