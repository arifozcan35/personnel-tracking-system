package com.personneltrackingsystem.service.impl;

import com.personneltrackingsystem.dto.DtoGate;
import com.personneltrackingsystem.dto.DtoGateIU;
import com.personneltrackingsystem.entity.Gate;
import com.personneltrackingsystem.entity.Unit;
import com.personneltrackingsystem.exception.BaseException;
import com.personneltrackingsystem.exception.ValidationException;
import com.personneltrackingsystem.mapper.GateMapper;
import com.personneltrackingsystem.repository.GateRepository;
import com.personneltrackingsystem.service.UnitService;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GateServiceImplTest {

    @Mock
    private GateRepository gateRepository;

    @Mock
    private UnitService unitService;

    @Mock
    private GateMapper gateMapper;

    @InjectMocks
    private GateServiceImpl gateService;

    private Gate gate;
    private DtoGate dtoGate;
    private DtoGateIU dtoGateIU;
    private Unit unit;
    private final Long gateId = 1L;
    private final Long unitId = 1L;
    private final String gateName = "Test Gate";

    @BeforeEach
    void setUp() {
        unit = new Unit();
        unit.setUnitId(unitId);
        unit.setUnitName("Test Unit");

        gate = new Gate();
        gate.setGateId(gateId);
        gate.setGateName(gateName);
        gate.setMainEntrance(true);
        gate.setUnitId(unit);

        dtoGate = new DtoGate();
        dtoGate.setGateName(gateName);

        dtoGateIU = new DtoGateIU();
        dtoGateIU.setGateName(gateName);
        dtoGateIU.setMainEntrance(true);
        dtoGateIU.setUnitId(unitId);
    }

    @Test
    void checkIfGateExists_whenGateExists_shouldReturnGate() {
        when(gateRepository.findById(gateId)).thenReturn(Optional.of(gate));

        Gate result = gateService.checkIfGateExists(gateId);

        assertEquals(gateId, result.getGateId());
        assertEquals(gateName, result.getGateName());
        verify(gateRepository).findById(gateId);
    }

    @Test
    void checkIfGateExists_whenGateDoesNotExist_shouldThrowException() {
        when(gateRepository.findById(gateId)).thenReturn(Optional.empty());

        assertThrows(BaseException.class, () -> gateService.checkIfGateExists(gateId));
        verify(gateRepository).findById(gateId);
    }

    @Test
    void getAllGates_shouldReturnAllGates() {
        List<Gate> gates = Arrays.asList(gate);
        List<DtoGate> dtoGates = Arrays.asList(dtoGate);

        when(gateRepository.findAll()).thenReturn(gates);
        when(gateMapper.gateListToDtoGateList(gates)).thenReturn(dtoGates);

        List<DtoGate> result = gateService.getAllGates();

        assertEquals(1, result.size());
        assertEquals(gateName, result.get(0).getGateName());
        verify(gateRepository).findAll();
        verify(gateMapper).gateListToDtoGateList(gates);
    }

    @Test
    void getGateById_whenGateExists_shouldReturnGate() {
        when(gateRepository.findById(gateId)).thenReturn(Optional.of(gate));
        when(gateMapper.gateToDtoGate(gate)).thenReturn(dtoGate);

        Optional<DtoGate> result = gateService.getGateById(gateId);

        assertTrue(result.isPresent());
        assertEquals(gateName, result.get().getGateName());
        verify(gateRepository).findById(gateId);
        verify(gateMapper).gateToDtoGate(gate);
    }

    @Test
    void getGateById_whenGateDoesNotExist_shouldThrowException() {
        when(gateRepository.findById(gateId)).thenReturn(Optional.empty());

        assertThrows(BaseException.class, () -> gateService.getGateById(gateId));
        verify(gateRepository).findById(gateId);
        verify(gateMapper, never()).gateToDtoGate(any());
    }

    @Test
    void getOneGate_whenGateExists_shouldReturnGate() {
        when(gateRepository.findById(gateId)).thenReturn(Optional.of(gate));
        when(gateMapper.gateToDtoGate(gate)).thenReturn(dtoGate);

        DtoGate result = gateService.getOneGate(gateId);

        assertEquals(gateName, result.getGateName());
        verify(gateRepository).findById(gateId);
        verify(gateMapper).gateToDtoGate(gate);
    }

    @Test
    void getOneGate_whenGateDoesNotExist_shouldThrowException() {
        when(gateRepository.findById(gateId)).thenReturn(Optional.empty());

        assertThrows(BaseException.class, () -> gateService.getOneGate(gateId));
        verify(gateRepository).findById(gateId);
        verify(gateMapper, never()).gateToDtoGate(any());
    }

    @Test
    void saveOneGate_withValidData_shouldSaveGate() {
        when(unitService.checkIfUnitExists(unitId)).thenReturn(unit);
        when(gateRepository.existsByGateName(gateName)).thenReturn(false);
        when(gateMapper.dtoGateIUToGate(dtoGateIU)).thenReturn(gate);
        when(gateRepository.save(gate)).thenReturn(gate);
        when(gateMapper.gateToDtoGate(gate)).thenReturn(dtoGate);

        DtoGate result = gateService.saveOneGate(dtoGateIU);

        assertEquals(gateName, result.getGateName());
        verify(gateRepository).existsByGateName(gateName);
        verify(unitService).checkIfUnitExists(unitId);
        verify(gateMapper).dtoGateIUToGate(dtoGateIU);
        verify(gateRepository).save(gate);
        verify(gateMapper).gateToDtoGate(gate);
    }

    @Test
    void saveOneGate_withEmptyGateName_shouldThrowException() {
        dtoGateIU.setGateName(null);

        assertThrows(ValidationException.class, () -> gateService.saveOneGate(dtoGateIU));
        verify(gateRepository, never()).save(any());
    }

    @Test
    void saveOneGate_withExistingGateName_shouldThrowException() {
        when(gateRepository.existsByGateName(gateName)).thenReturn(true);

        assertThrows(ValidationException.class, () -> gateService.saveOneGate(dtoGateIU));
        verify(gateRepository).existsByGateName(gateName);
        verify(gateRepository, never()).save(any());
    }

    @Test
    void updateOneGate_withValidData_shouldUpdateGate() {
        String newGateName = "Updated Gate";
        DtoGateIU updatedGateIU = new DtoGateIU();
        updatedGateIU.setGateName(newGateName);
        updatedGateIU.setMainEntrance(false);
        updatedGateIU.setUnitId(unitId);

        Gate updatedGate = new Gate();
        updatedGate.setGateId(gateId);
        updatedGate.setGateName(newGateName);
        updatedGate.setMainEntrance(false);
        updatedGate.setUnitId(unit);

        DtoGate updatedDtoGate = new DtoGate();
        updatedDtoGate.setGateName(newGateName);

        when(gateRepository.findById(gateId)).thenReturn(Optional.of(gate));
        when(gateRepository.existsByGateName(newGateName)).thenReturn(false);
        when(unitService.checkIfUnitExists(unitId)).thenReturn(unit);
        when(gateRepository.save(any(Gate.class))).thenReturn(updatedGate);
        when(gateMapper.gateToDtoGate(updatedGate)).thenReturn(updatedDtoGate);

        DtoGate result = gateService.updateOneGate(gateId, updatedGateIU);

        assertEquals(newGateName, result.getGateName());
        verify(gateRepository).findById(gateId);
        verify(unitService).checkIfUnitExists(unitId);
        verify(gateRepository).save(any(Gate.class));
        verify(gateMapper).gateToDtoGate(any(Gate.class));
    }

    @Test
    void updateOneGate_withExistingGateName_shouldThrowException() {
        String newGateName = "Existing Gate";
        DtoGateIU updatedGateIU = new DtoGateIU();
        updatedGateIU.setGateName(newGateName);

        when(gateRepository.findById(gateId)).thenReturn(Optional.of(gate));
        when(gateRepository.existsByGateName(newGateName)).thenReturn(true);

        assertThrows(ValidationException.class, () -> gateService.updateOneGate(gateId, updatedGateIU));
        verify(gateRepository).findById(gateId);
        verify(gateRepository).existsByGateName(newGateName);
        verify(gateRepository, never()).save(any());
    }

    @Test
    void updateOneGate_whenGateDoesNotExist_shouldThrowException() {
        when(gateRepository.findById(gateId)).thenReturn(Optional.empty());

        assertThrows(BaseException.class, () -> gateService.updateOneGate(gateId, dtoGateIU));
        verify(gateRepository).findById(gateId);
        verify(gateRepository, never()).save(any());
    }

    @Test
    void deleteOneGate_whenGateExists_shouldDeleteGate() {
        when(gateRepository.findById(gateId)).thenReturn(Optional.of(gate));

        gateService.deleteOneGate(gateId);

        verify(gateRepository).findById(gateId);
        verify(gateRepository).delete(gate);
    }

    @Test
    void deleteOneGate_whenGateDoesNotExist_shouldThrowException() {
        when(gateRepository.findById(gateId)).thenReturn(Optional.empty());

        assertThrows(BaseException.class, () -> gateService.deleteOneGate(gateId));
        verify(gateRepository).findById(gateId);
        verify(gateRepository, never()).delete(any());
    }
} 