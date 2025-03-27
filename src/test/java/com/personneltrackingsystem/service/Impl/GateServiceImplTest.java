package com.personneltrackingsystem.service.Impl;

import com.personneltrackingsystem.dto.DtoGate;
import com.personneltrackingsystem.dto.DtoGateIU;
import com.personneltrackingsystem.entity.Gate;
import com.personneltrackingsystem.entity.Personel;
import com.personneltrackingsystem.exception.BaseException;
import com.personneltrackingsystem.exception.MessageResolver;
import com.personneltrackingsystem.mapper.GateMapper;
import com.personneltrackingsystem.repository.GateRepository;
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

@ExtendWith(MockitoExtension.class)
public class GateServiceImplTest {

    @Mock
    private GateRepository gateRepository;

    @Mock
    private MessageResolver messageResolver;

    @Mock
    private GateMapper gateMapper;

    @InjectMocks
    private GateServiceImpl gateService;

    private Gate mockGate;
    private DtoGate mockDtoGate;
    private DtoGateIU mockDtoGateIU;
    private Personel mockPersonel;

    @BeforeEach
    void setUp() {
        mockGate = new Gate();
        mockGate.setGateId(1L);
        mockGate.setGateName("Main Gate");

        mockDtoGate = new DtoGate();
        mockDtoGate.setGateId(1L);
        mockDtoGate.setGateName("Main Gate");

        mockDtoGateIU = new DtoGateIU();
        mockDtoGateIU.setGateName("Main Gate");

        mockPersonel = new Personel();
        mockPersonel.setPersonelId(1L);
        mockPersonel.setName("Test Personnel");
    }

    @Test
    void testFindById_Exists() {
        when(gateRepository.findById(1L)).thenReturn(Optional.of(mockGate));

        Optional<Gate> result = gateService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(mockGate, result.get());
        verify(gateRepository).findById(1L);
    }

    @Test
    void testGetAllGates() {
        List<Gate> gates = Arrays.asList(mockGate);
        List<DtoGate> dtoGates = Arrays.asList(mockDtoGate);

        when(gateRepository.findAll()).thenReturn(gates);
        when(gateMapper.gatesToDtoGates(gates)).thenReturn(dtoGates);

        List<DtoGate> result = gateService.getAllGates();

        assertEquals(dtoGates, result);
        verify(gateRepository).findAll();
        verify(gateMapper).gatesToDtoGates(gates);
    }

    @Test
    void testGetOneGate_Exists() {
        when(gateRepository.findById(1L)).thenReturn(Optional.of(mockGate));
        when(gateMapper.gateToDtoGate(mockGate)).thenReturn(mockDtoGate);

        DtoGate result = gateService.getOneGate(1L);

        assertEquals(mockDtoGate, result);
        verify(gateRepository).findById(1L);
        verify(gateMapper).gateToDtoGate(mockGate);
    }

    @Test
    void testGetOneGate_NotExists() {
        when(gateRepository.findById(1L)).thenReturn(Optional.empty());
        when(messageResolver.toString()).thenReturn("Error Message");

        assertThrows(BaseException.class, () -> gateService.getOneGate(1L));
        verify(gateRepository).findById(1L);
    }

    @Test
    void testSaveOneGate_Success() {
        when(gateMapper.dtoGateIUToGate(mockDtoGateIU)).thenReturn(mockGate);
        when(gateRepository.save(mockGate)).thenReturn(mockGate);
        when(gateMapper.gateToDtoGate(mockGate)).thenReturn(mockDtoGate);

        DtoGate result = gateService.saveOneGate(mockDtoGateIU);

        assertEquals(mockDtoGate, result);
        verify(gateRepository).save(mockGate);
    }

    @Test
    void testSaveOneGate_MissingName() {
        mockDtoGateIU.setGateName(null);
        when(messageResolver.toString()).thenReturn("Error Message");

        assertThrows(BaseException.class, () -> gateService.saveOneGate(mockDtoGateIU));
    }

    @Test
    void testUpdateOneGate_Success() {
        Gate existingGate = new Gate();
        existingGate.setGateId(1L);
        existingGate.setGateName("Old Gate Name");

        when(gateRepository.findById(1L)).thenReturn(Optional.of(existingGate));
        when(gateRepository.save(existingGate)).thenReturn(existingGate);
        when(gateMapper.gateToDtoGate(existingGate)).thenReturn(mockDtoGate);

        DtoGate result = gateService.updateOneGate(1L, mockDtoGateIU);

        assertEquals(mockDtoGate, result);
        assertEquals("Main Gate", existingGate.getGateName());
        verify(gateRepository).findById(1L);
        verify(gateRepository).save(existingGate);
    }

    @Test
    void testUpdateOneGate_NotExists() {
        when(gateRepository.findById(1L)).thenReturn(Optional.empty());
        when(messageResolver.toString()).thenReturn("Error Message");

        assertThrows(BaseException.class, () -> gateService.updateOneGate(1L, mockDtoGateIU));
        verify(gateRepository).findById(1L);
    }

    @Test
    void testDeleteOneGate_Success() {
        when(gateRepository.findById(1L)).thenReturn(Optional.of(mockGate));

        gateService.deleteOneGate(1L);

        verify(gateRepository).updatePersonelGateReferences(1L);
        verify(gateRepository).delete(mockGate);
    }

    @Test
    void testDeleteOneGate_NotExists() {
        when(gateRepository.findById(1L)).thenReturn(Optional.empty());
        when(messageResolver.toString()).thenReturn("Error Message");

        assertThrows(BaseException.class, () -> gateService.deleteOneGate(1L));
        verify(gateRepository).findById(1L);
    }

    @Test
    void testGetPersonelsByGateId_Exists() {
        List<Personel> mockPersonels = new ArrayList<>();
        mockPersonels.add(mockPersonel);

        mockGate.setPersonels(mockPersonels);

        when(gateRepository.findById(1L)).thenReturn(Optional.of(mockGate));

        Set<Personel> result = gateService.getPersonelsByGateId(1L);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(gateRepository).findById(1L);
    }

    @Test
    void testGetPersonelsByGateId_NotExists() {
        when(gateRepository.findById(1L)).thenReturn(Optional.empty());
        when(messageResolver.toString()).thenReturn("Error Message");

        assertThrows(BaseException.class, () -> gateService.getPersonelsByGateId(1L));
        verify(gateRepository).findById(1L);
    }

    @Test
    void testPassGate_Successful() {
        when(gateRepository.findPrsnlById(1L)).thenReturn(Optional.of(mockPersonel));
        when(gateRepository.findById(1L)).thenReturn(Optional.of(mockGate));

        ResponseEntity<String> result = gateService.passGate(1L, 1L);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals("Personnel entered the gate!", result.getBody());
    }

    @Test
    void testPassGate_PersonnelNotFound() {
        when(gateRepository.findPrsnlById(1L)).thenReturn(Optional.empty());

        ResponseEntity<String> result = gateService.passGate(1L, 1L);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("This personnel is not available! Entry from outside the institution is prohibited!", result.getBody());
    }

    @Test
    void testPassGate_GateNotFound() {
        when(gateRepository.findPrsnlById(1L)).thenReturn(Optional.of(mockPersonel));
        when(gateRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<String> result = gateService.passGate(1L, 1L);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("The gate you want to enter is not available!", result.getBody());
    }

    @Test
    void testPassGate_UnauthorizedEntry() {
        when(gateRepository.findPrsnlById(1L)).thenReturn(Optional.of(mockPersonel));
        when(gateRepository.findById(2L)).thenReturn(Optional.of(new Gate()));

        ResponseEntity<String> result = gateService.passGate(2L, 1L);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Personnel is not authorized to enter this gate!", result.getBody());
    }
}