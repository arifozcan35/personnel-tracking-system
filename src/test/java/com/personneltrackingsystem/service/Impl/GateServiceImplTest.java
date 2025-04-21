package com.personneltrackingsystem.service.Impl;

import com.personneltrackingsystem.dto.DtoGate;
import com.personneltrackingsystem.dto.DtoGateIU;
import com.personneltrackingsystem.entity.Gate;
import com.personneltrackingsystem.entity.Personel;
import com.personneltrackingsystem.exception.BaseException;
import com.personneltrackingsystem.exception.MessageResolver;
import com.personneltrackingsystem.mapper.GateMapper;
import com.personneltrackingsystem.repository.GateRepository;
import com.personneltrackingsystem.validator.GateValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GateServiceImplTest {

    @Mock
    private GateRepository gateRepository;

    @Mock
    private MessageResolver messageResolver;

    @Mock
    private GateMapper gateMapper;

    @Mock
    private GateValidator gateValidator;

    @InjectMocks
    private GateServiceImpl gateService;

    private Gate gate;
    private DtoGate dtoGate;
    private DtoGateIU dtoGateIU;
    private List<Gate> gateList;
    private Set<Personel> personelSet;
    private Personel personel;

    @BeforeEach
    void setUp() {
        // Initialize test data
        gate = new Gate();
        gate.setGateId(1L);
        gate.setGateName("Test Gate");

        dtoGate = new DtoGate();
        dtoGate.setGateId(1L);
        dtoGate.setGateName("Test Gate");

        dtoGateIU = new DtoGateIU();
        dtoGateIU.setGateName("Updated Gate Name");

        gateList = new ArrayList<>();
        gateList.add(gate);

        personelSet = new HashSet<>();
        personel = new Personel();
        personel.getGate().setGateId(1L);
        Gate personelGate = new Gate();
        personelGate.setGateId(1L);
        personel.setGate(personelGate);
        personelSet.add(personel);
        gate.setPersonels((List<Personel>) personelSet);
    }

    @Test
    void findById_WhenGateExists_ShouldReturnDtoGateIU() {
        // Arrange
        when(gateRepository.findById(anyLong())).thenReturn(Optional.of(gate));
        when(gateMapper.gateToDtoGateIU(any(Gate.class))).thenReturn(dtoGateIU);

        // Act
        Optional<DtoGateIU> result = gateService.findById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(dtoGateIU, result.get());
        verify(gateRepository).findById(1L);
        verify(gateMapper).gateToDtoGateIU(gate);
    }

    @Test
    void findById_WhenGateDoesNotExist_ShouldThrowEntityNotFoundException() {
        // Arrange
        when(gateRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> gateService.findById(1L));
        verify(gateRepository).findById(1L);
        verify(gateMapper, never()).gateToDtoGateIU(any(Gate.class));
    }

    @Test
    void getAllGates_ShouldReturnAllGates() {
        // Arrange
        when(gateRepository.findAll()).thenReturn(gateList);
        when(gateMapper.gateToDtoGate(any(Gate.class))).thenReturn(dtoGate);

        // Act
        List<DtoGate> result = gateService.getAllGates();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(dtoGate, result.get(0));
        verify(gateRepository).findAll();
        verify(gateMapper).gateToDtoGate(gate);
    }

    @Test
    void getOneGate_WhenGateExists_ShouldReturnGate() {
        // Arrange
        when(gateRepository.findById(anyLong())).thenReturn(Optional.of(gate));
        when(gateMapper.gateToDtoGate(any(Gate.class))).thenReturn(dtoGate);

        // Act
        DtoGate result = gateService.getOneGate(1L);

        // Assert
        assertNotNull(result);
        assertEquals(dtoGate, result);
        verify(gateRepository).findById(1L);
        verify(gateMapper).gateToDtoGate(gate);
    }

    @Test
    void getOneGate_WhenGateDoesNotExist_ShouldThrowBaseException() {
        // Arrange
        when(gateRepository.findById(anyLong())).thenReturn(Optional.empty());
        when(messageResolver.toString()).thenReturn("Gate not found");

        // Act & Assert
        BaseException exception = assertThrows(BaseException.class, () -> gateService.getOneGate(1L));
        verify(gateRepository).findById(1L);
        verify(messageResolver).toString();
        verify(gateMapper, never()).gateToDtoGate(any(Gate.class));
    }

    @Test
    void saveOneGate_ShouldSaveAndReturnGate() {
        // Arrange
        when(gateMapper.dtoGateToGate(any(DtoGate.class))).thenReturn(gate);
        when(gateRepository.save(any(Gate.class))).thenReturn(gate);
        when(gateMapper.gateToDtoGate(any(Gate.class))).thenReturn(dtoGate);
        doNothing().when(gateValidator).checkIfGateAlreadyExists(any(DtoGate.class));

        // Act
        DtoGate result = gateService.saveOneGate(dtoGate);

        // Assert
        assertNotNull(result);
        assertEquals(dtoGate, result);
        verify(gateValidator).checkIfGateAlreadyExists(dtoGate);
        verify(gateMapper).dtoGateToGate(dtoGate);
        verify(gateRepository).save(gate);
        verify(gateMapper).gateToDtoGate(gate);
    }

    @Test
    void updateOneGate_WhenGateExists_ShouldUpdateAndReturnGate() {
        // Arrange
        when(gateRepository.findById(anyLong())).thenReturn(Optional.of(gate));
        when(gateRepository.save(any(Gate.class))).thenReturn(gate);
        when(gateMapper.gateToDtoGate(any(Gate.class))).thenReturn(dtoGate);

        // Act
        DtoGate result = gateService.updateOneGate(1L, dtoGateIU);

        // Assert
        assertNotNull(result);
        assertEquals(dtoGate, result);
        verify(gateRepository).findById(1L);
        verify(gateRepository).save(gate);
        verify(gateMapper).gateToDtoGate(gate);
        assertEquals(dtoGateIU.getGateName(), gate.getGateName());
    }

    @Test
    void updateOneGate_WhenGateDoesNotExist_ShouldThrowBaseException() {
        // Arrange
        when(gateRepository.findById(anyLong())).thenReturn(Optional.empty());
        when(messageResolver.toString()).thenReturn("Gate not found");

        // Act & Assert
        BaseException exception = assertThrows(BaseException.class, () -> gateService.updateOneGate(1L, dtoGateIU));
        verify(gateRepository).findById(1L);
        verify(messageResolver).toString();
        verify(gateRepository, never()).save(any(Gate.class));
        verify(gateMapper, never()).gateToDtoGate(any(Gate.class));
    }

    @Test
    void deleteOneGate_WhenGateExists_ShouldDeleteGate() {
        // Arrange
        when(gateRepository.findById(anyLong())).thenReturn(Optional.of(gate));
        doNothing().when(gateRepository).updatePersonelGateReferences(anyLong());
        doNothing().when(gateRepository).delete(any(Gate.class));

        // Act
        gateService.deleteOneGate(1L);

        // Assert
        verify(gateRepository).findById(1L);
        verify(gateRepository).updatePersonelGateReferences(1L);
        verify(gateRepository).delete(gate);
    }

    @Test
    void deleteOneGate_WhenGateDoesNotExist_ShouldThrowBaseException() {
        // Arrange
        when(gateRepository.findById(anyLong())).thenReturn(Optional.empty());
        when(messageResolver.toString()).thenReturn("Gate not found");

        // Act & Assert
        BaseException exception = assertThrows(BaseException.class, () -> gateService.deleteOneGate(1L));
        verify(gateRepository).findById(1L);
        verify(messageResolver).toString();
        verify(gateRepository, never()).updatePersonelGateReferences(anyLong());
        verify(gateRepository, never()).delete(any(Gate.class));
    }

    @Test
    void getPersonelsByGateId_WhenGateExists_ShouldReturnPersonels() {
        // Arrange
        when(gateRepository.findById(anyLong())).thenReturn(Optional.of(gate));

        // Act
        Set<Personel> result = gateService.getPersonelsByGateId(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(gateRepository).findById(1L);
    }

    @Test
    void getPersonelsByGateId_WhenGateDoesNotExist_ShouldThrowBaseException() {
        // Arrange
        when(gateRepository.findById(anyLong())).thenReturn(Optional.empty());
        when(messageResolver.toString()).thenReturn("Gate not found");

        // Act & Assert
        BaseException exception = assertThrows(BaseException.class, () -> gateService.getPersonelsByGateId(1L));
        verify(gateRepository).findById(1L);
        verify(messageResolver).toString();
    }

    @Test
    void passGate_WhenPersonelAndGateExistAndPersonelAuthorized_ShouldReturnSuccess() {
        // Arrange
        when(gateRepository.findPrsnlById(anyLong())).thenReturn(Optional.of(personel));
        when(gateRepository.findById(anyLong())).thenReturn(Optional.of(gate));

        // Act
        ResponseEntity<String> result = gateService.passGate(1L, 1L);

        // Assert
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals("Personnel entered the gate!", result.getBody());
        verify(gateRepository).findPrsnlById(1L);
        verify(gateRepository).findById(1L);
    }

    @Test
    void passGate_WhenPersonelDoesNotExist_ShouldThrowBaseException() {
        // Arrange
        when(gateRepository.findPrsnlById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        BaseException exception = assertThrows(BaseException.class, () -> gateService.passGate(1L, 1L));
        verify(gateRepository).findPrsnlById(1L);
        verify(gateRepository, never()).findById(anyLong());
    }

    @Test
    void passGate_WhenGateDoesNotExist_ShouldThrowBaseException() {
        // Arrange
        when(gateRepository.findPrsnlById(anyLong())).thenReturn(Optional.of(personel));
        when(gateRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        BaseException exception = assertThrows(BaseException.class, () -> gateService.passGate(1L, 1L));
        verify(gateRepository).findPrsnlById(1L);
        verify(gateRepository).findById(1L);
    }

    @Test
    void passGate_WhenPersonelNotAuthorized_ShouldThrowBaseException() {
        // Arrange
        Gate differentGate = new Gate();
        differentGate.setGateId(2L);

        when(gateRepository.findPrsnlById(anyLong())).thenReturn(Optional.of(personel));
        when(gateRepository.findById(anyLong())).thenReturn(Optional.of(differentGate));

        // Act & Assert
        BaseException exception = assertThrows(BaseException.class, () -> gateService.passGate(2L, 1L));
        verify(gateRepository).findPrsnlById(1L);
        verify(gateRepository).findById(2L);
    }
}