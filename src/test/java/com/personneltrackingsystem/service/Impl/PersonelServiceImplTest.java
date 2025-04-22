package com.personneltrackingsystem.service.Impl;

import static org.junit.jupiter.api.Assertions.*;

import com.personneltrackingsystem.dto.*;
import com.personneltrackingsystem.entity.Gate;
import com.personneltrackingsystem.entity.Personel;
import com.personneltrackingsystem.entity.Unit;
import com.personneltrackingsystem.entity.Work;
import com.personneltrackingsystem.exception.*;
import com.personneltrackingsystem.mapper.GateMapper;
import com.personneltrackingsystem.mapper.PersonelMapper;
import com.personneltrackingsystem.mapper.UnitMapper;
import com.personneltrackingsystem.repository.PersonelRepository;
import com.personneltrackingsystem.service.GateService;
import com.personneltrackingsystem.service.UnitService;
import com.personneltrackingsystem.service.WorkService;
import com.personneltrackingsystem.validator.PersonelValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Duration;
import java.time.LocalTime;

import static org.mockito.Mockito.*;

import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

import java.util.ArrayList;
import java.util.HashMap;

import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
public class PersonelServiceImplTest {

    @Mock
    private PersonelRepository personelRepository;

    @Mock
    private WorkService workServiceImpl;

    @Mock
    private UnitService unitServiceImpl;

    @Mock
    private GateService gateServiceImpl;

    @Mock
    private MessageResolver messageResolver;

    @Mock
    private PersonelMapper personelMapper;

    @Mock
    private UnitMapper unitMapper;

    @Mock
    private GateMapper gateMapper;

    @Mock
    private PersonelValidator personelValidator;

    @InjectMocks
    private PersonelServiceImpl personelService;

    private Personel personel;
    private DtoPersonel dtoPersonel;
    private DtoPersonelIU dtoPersonelIU;
    private List<Personel> personelList;
    private List<DtoPersonel> dtoPersonelList;
    private Work work;
    private Unit unit;
    private Gate gate;
    private DtoUnit dtoUnit;
    private DtoGate dtoGate;
    private DtoUnitIU dtoUnitIU;
    private DtoGateIU dtoGateIU;
    private DtoWork dtoWork;

    @BeforeEach
    void setUp() {
        // Initialize test data
        personel = new Personel();
        personel.setPersonelId(1L);
        personel.setName("Test User");
        personel.setEmail("test@example.com");
        personel.setAdministrator(false);
        personel.setSalary(30000.0);

        unit = new Unit();
        unit.setUnitId(1L);
        unit.setUnitName("IT");
        personel.setUnit(unit);

        gate = new Gate();
        gate.setGateId(1L);
        gate.setGateName("Main Gate");
        personel.setGate(gate);

        work = new Work();
        work.setWorkId(1L);
        work.setCheckInTime(LocalTime.of(9, 0));
        work.setCheckOutTime(LocalTime.of(18, 0));
        work.setIsWorkValid(true);
        personel.setWork(work);

        dtoWork = new DtoWork();
        dtoWork.setCheckInTime(LocalTime.of(9, 0));
        dtoWork.setCheckOutTime(LocalTime.of(18, 0));


        dtoPersonel = new DtoPersonel();
        dtoPersonel.setName("Test User");
        dtoPersonel.setEmail("test@example.com");
        dtoPersonel.setSalary(30000.0);

        dtoPersonelIU = new DtoPersonelIU();
        dtoPersonelIU.setName("Test User");
        dtoPersonelIU.setEmail("test@example.com");
        dtoPersonelIU.setAdministrator(false);
        dtoPersonelIU.setSalary(30000.0);

        dtoUnit = new DtoUnit();
        dtoUnit.setUnitId(1L);
        dtoUnit.setBirimIsim("IT");

        dtoUnitIU = new DtoUnitIU();
        dtoUnitIU.setUnitId(1L);
        dtoUnitIU.setBirimIsim("IT");

        dtoGate = new DtoGate();
        dtoGate.setGateId(1L);
        dtoGate.setGateName("Main Gate");

        dtoGateIU = new DtoGateIU();
        dtoGateIU.setGateId(1L);
        dtoGateIU.setGateName("Main Gate");

        dtoPersonelIU.setUnit(dtoUnit);
        dtoPersonelIU.setGate(dtoGate);
        dtoPersonelIU.setWork(dtoWork);

        personelList = new ArrayList<>();
        personelList.add(personel);

        dtoPersonelList = new ArrayList<>();
        dtoPersonelList.add(dtoPersonel);
    }

    @Test
    void getAllPersonels_ShouldReturnAllPersonels() {
        // Given
        when(personelRepository.findAll()).thenReturn(personelList);
        when(personelMapper.personelsToDtoPersonels(personelList)).thenReturn(dtoPersonelList);

        // When
        List<DtoPersonel> result = personelService.getAllPersonels();

        // Then
        assertEquals(dtoPersonelList, result);
        verify(personelRepository).findAll();
        verify(personelMapper).personelsToDtoPersonels(personelList);
    }

    @Test
    void getAOnePersonel_WhenPersonelExists_ShouldReturnPersonel() {
        // Given
        Long personelId = 1L;
        when(personelRepository.findById(personelId)).thenReturn(Optional.of(personel));
        when(personelMapper.personelToDtoPersonel(personel)).thenReturn(dtoPersonel);

        // When
        DtoPersonel result = personelService.getAOnePersonel(personelId);

        // Then
        assertEquals(dtoPersonel, result);
        verify(personelRepository).findById(personelId);
        verify(personelMapper).personelToDtoPersonel(personel);
    }

    @Test
    void getAOnePersonel_WhenPersonelDoesNotExist_ShouldThrowException() {
        // Given
        Long personelId = 999L;
        when(personelRepository.findById(personelId)).thenReturn(Optional.empty());

        // When
        try {
            personelService.getAOnePersonel(personelId);
            fail("Expected exception was not thrown");
        } catch (Exception e) {
            // Then
            verify(personelRepository).findById(personelId);
            verify(personelMapper, never()).personelToDtoPersonel(any(Personel.class));
        }
    }

    @Test
    void saveOnePersonel_WhenValidData_ShouldSavePersonel() {
        // Given
        when(personelMapper.dtoPersonelIUToPersonel(dtoPersonelIU)).thenReturn(personel);
        when(personelRepository.save(personel)).thenReturn(personel);
        doNothing().when(personelValidator).validatePersonelForSave(dtoPersonelIU);

        // When
        ResponseEntity<String> response = personelService.saveOnePersonel(dtoPersonelIU);

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(response.getBody().contains("Personnel registered successfully"));

        verify(personelValidator).validatePersonelForSave(dtoPersonelIU);
        verify(personelMapper).dtoPersonelIUToPersonel(dtoPersonelIU);
        verify(personelRepository).save(personel);
    }

    @Test
    void saveOnePersonel_WhenDataIntegrityViolation_ShouldReturnBadRequest() {
        // Given
        when(personelMapper.dtoPersonelIUToPersonel(dtoPersonelIU)).thenReturn(personel);
        when(personelRepository.save(personel)).thenThrow(DataIntegrityViolationException.class);
        doNothing().when(personelValidator).validatePersonelForSave(dtoPersonelIU);

        // When
        ResponseEntity<String> response = personelService.saveOnePersonel(dtoPersonelIU);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Could not save personnel due to a data integrity violation.", response.getBody());

        verify(personelValidator).validatePersonelForSave(dtoPersonelIU);
        verify(personelMapper).dtoPersonelIUToPersonel(dtoPersonelIU);
        verify(personelRepository).save(personel);
    }

    @Test
    void updateOnePersonel_WhenPersonelExists_ShouldUpdatePersonel() {
        // Given
        Long personelId = 1L;
        DtoPersonelIU updateDto = new DtoPersonelIU();
        updateDto.setName("Updated Name");
        updateDto.setEmail("updated@example.com");
        updateDto.setAdministrator(true);
        updateDto.setSalary(40000.0);

        when(personelRepository.findById(personelId)).thenReturn(Optional.of(personel));
        when(personelRepository.save(personel)).thenReturn(personel);
        doNothing().when(personelValidator).updatePersonelCheckUniqueEmail(eq(personelId), any(DtoPersonelIU.class));

        // When
        ResponseEntity<String> response = personelService.updateOnePersonel(personelId, updateDto);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("Personnel updated successfully"));

        verify(personelRepository).findById(personelId);
        verify(personelRepository).save(personel);
        verify(personelValidator).updatePersonelCheckUniqueEmail(eq(personelId), any(DtoPersonelIU.class));

        // Verify personel was updated
        assertEquals("Updated Name", personel.getName());
        assertEquals("updated@example.com", personel.getEmail());
        assertEquals(true, personel.getAdministrator());
    }

    @Test
    void updateOnePersonel_WhenPersonelDoesNotExist_ShouldReturnNotFound() {
        // Given
        Long personelId = 999L;
        when(personelRepository.findById(personelId)).thenReturn(Optional.empty());

        // When
        ResponseEntity<String> response = personelService.updateOnePersonel(personelId, dtoPersonelIU);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("No personnel found!", response.getBody());

        verify(personelRepository).findById(personelId);
        verify(personelRepository, never()).save(any(Personel.class));
    }

    @Test
    void updateOnePersonel_WhenUnitNotFound_ShouldReturnNotFound() {
        // Given
        Long personelId = 1L;
        DtoPersonelIU updateDto = new DtoPersonelIU();
        updateDto.setUnit(dtoUnit);

        when(personelRepository.findById(personelId)).thenReturn(Optional.of(personel));
        when(unitServiceImpl.findById(dtoUnitIU.getUnitId())).thenReturn(Optional.empty());

        // When
        ResponseEntity<String> response = personelService.updateOnePersonel(personelId, updateDto);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("You have not selected a suitable unit!", response.getBody());

        verify(personelRepository).findById(personelId);
        verify(unitServiceImpl).findById(dtoUnitIU.getUnitId());
        verify(personelRepository, never()).save(any(Personel.class));
    }

    @Test
    void updateOnePersonel_WhenGateNotFound_ShouldReturnNotFound() {
        // Given
        Long personelId = 1L;
        DtoPersonelIU updateDto = new DtoPersonelIU();
        updateDto.setGate(dtoGate);

        when(personelRepository.findById(personelId)).thenReturn(Optional.of(personel));
        when(gateServiceImpl.findById(dtoGateIU.getGateId())).thenReturn(Optional.empty());

        // When
        ResponseEntity<String> response = personelService.updateOnePersonel(personelId, updateDto);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("The specified gate could not be found!", response.getBody());

        verify(personelRepository).findById(personelId);
        verify(gateServiceImpl).findById(dtoGateIU.getGateId());
        verify(personelRepository, never()).save(any(Personel.class));
    }

    @Test
    void updateOnePersonel_WhenWorkIsUpdated_ShouldUpdateWork() {
        // Given
        Long personelId = 1L;
        DtoWork updatedWork = new DtoWork();
        updatedWork.setCheckInTime(LocalTime.of(8, 0));
        updatedWork.setCheckOutTime(LocalTime.of(16, 0));

        DtoPersonelIU updateDto = new DtoPersonelIU();
        updateDto.setWork(updatedWork);

        Personel existingPersonel = new Personel();
        existingPersonel.setPersonelId(personelId);
        existingPersonel.setSalary(30000.0);
        existingPersonel.setWork(work);

        when(personelRepository.findById(personelId)).thenReturn(Optional.of(existingPersonel));
        when(personelRepository.save(existingPersonel)).thenReturn(existingPersonel);
        doNothing().when(personelValidator).updatePersonelCheckEntryAndExit(any(), any());
        when(workServiceImpl.save(work)).thenReturn(work);

        // When
        ResponseEntity<String> response = personelService.updateOnePersonel(personelId, updateDto);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("Personnel updated successfully"));

        verify(personelRepository).findById(personelId);
        verify(personelValidator).updatePersonelCheckEntryAndExit(updatedWork.getCheckInTime(), updatedWork.getCheckOutTime());
        verify(workServiceImpl).save(work);
        verify(personelRepository).save(existingPersonel);

        // Verify work was updated
        assertEquals(LocalTime.of(8, 0), existingPersonel.getWork().getCheckInTime());
        assertEquals(LocalTime.of(16, 0), existingPersonel.getWork().getCheckOutTime());
    }

    @Test
    void deleteOnePersonel_WhenPersonelExists_ShouldDeletePersonelAndWork() {
        // Given
        Long personelId = 1L;
        Long workId = 1L;

        Personel personelToDelete = new Personel();
        personelToDelete.setPersonelId(personelId);
        personelToDelete.setWork(work);
        work.setWorkId(workId);

        when(personelRepository.findById(personelId)).thenReturn(Optional.of(personelToDelete));
        doNothing().when(workServiceImpl).deleteById(workId);
        doNothing().when(personelRepository).deleteById(personelId);

        // When
        personelService.deleteOnePersonel(personelId);

        // Then
        verify(personelRepository).findById(personelId);
        verify(workServiceImpl).deleteById(workId);
        verify(personelRepository).deleteById(personelId);
    }


    @Test
    void deleteOnePersonel_WhenPersonelDoesNotExist_ShouldInvokeRepositoryAndFail() {
        // Given
        Long personelId = 999L;
        when(personelRepository.findById(personelId)).thenReturn(Optional.empty());

        // When
        try {
            personelService.deleteOnePersonel(personelId);
            fail("Expected exception was not thrown");
        } catch (Exception e) {
            // Then
            verify(personelRepository).findById(personelId);
            verify(workServiceImpl, never()).deleteById(anyLong());
            verify(personelRepository, never()).deleteById(anyLong());
        }
    }

    @Test
    void workHoursCalculate_WhenPersonelExists_ShouldCalculateAndUpdateSalary() {
        // Given
        Long personelId = 1L;
        LocalTime checkIn = LocalTime.of(9, 0);
        LocalTime checkOut = LocalTime.of(18, 0);
        Duration workTime = Duration.between(checkIn, checkOut);

        when(personelRepository.findById(personelId)).thenReturn(Optional.of(personel));
        when(personelValidator.calculateWorkTime(checkIn, checkOut)).thenReturn(workTime);
        when(personelValidator.isWorkValid(checkIn, checkOut)).thenReturn(true);
        when(personelRepository.save(personel)).thenReturn(personel);

        // When
        DtoPersonel result = personelService.workHoursCalculate(personelId);

        // Then
        assertEquals(dtoPersonel.getName(), result.getName());
        assertEquals(dtoPersonel.getEmail(), result.getEmail());
        assertEquals(dtoPersonel.getSalary(), result.getSalary());

        verify(personelRepository).findById(personelId);
        verify(personelValidator).calculateWorkTime(checkIn, checkOut);
        verify(personelValidator).isWorkValid(checkIn, checkOut);
        verify(personelRepository).save(personel);
    }

    @Test
    void workHoursCalculate_WhenPersonelDoesNotExist_ShouldInvokeRepositoryAndFail() {
        // Given
        Long personelId = 999L;
        when(personelRepository.findById(personelId)).thenReturn(Optional.empty());

        // When
        try {
            personelService.workHoursCalculate(personelId);
            fail("Expected exception was not thrown");
        } catch (Exception e) {
            // Then
            verify(personelRepository).findById(personelId);

            verify(personelRepository, never()).save(any(Personel.class));
        }
    }

    @Test
    void workHoursCalculate2_WhenWorkIsValid_ShouldNotApplyPenalty() {
        // Given
        Personel testPersonel = new Personel();
        testPersonel.setSalary(30000.0);
        testPersonel.setAdministrator(false);

        Work testWork = new Work();
        testWork.setCheckInTime(LocalTime.of(9, 0));
        testWork.setCheckOutTime(LocalTime.of(17, 0));
        testPersonel.setWork(testWork);

        Duration workTime = Duration.between(testWork.getCheckInTime(), testWork.getCheckOutTime());

        when(personelValidator.calculateWorkTime(testWork.getCheckInTime(), testWork.getCheckOutTime())).thenReturn(workTime);
        when(personelValidator.isWorkValid(testWork.getCheckInTime(), testWork.getCheckOutTime())).thenReturn(true);

        // When
        personelService.workHoursCalculate2(testPersonel);

        // Then
        assertEquals(true, testWork.getIsWorkValid());
        assertEquals(30000.0, testPersonel.getSalary());

        verify(personelValidator).calculateWorkTime(testWork.getCheckInTime(), testWork.getCheckOutTime());
        verify(personelValidator).isWorkValid(testWork.getCheckInTime(), testWork.getCheckOutTime());
    }

    @Test
    void workHoursCalculate2_WhenWorkIsInvalidAndNotAdmin_ShouldApplyPenalty() {
        // Given
        Personel testPersonel = new Personel();
        testPersonel.setSalary(30000.0);
        testPersonel.setAdministrator(false);

        Work testWork = new Work();
        testWork.setCheckInTime(LocalTime.of(10, 0));
        testWork.setCheckOutTime(LocalTime.of(16, 0));
        testPersonel.setWork(testWork);

        Duration workTime = Duration.between(testWork.getCheckInTime(), testWork.getCheckOutTime());
        double penalty = 1000.0;

        when(personelValidator.calculateWorkTime(testWork.getCheckInTime(), testWork.getCheckOutTime())).thenReturn(workTime);
        when(personelValidator.isWorkValid(testWork.getCheckInTime(), testWork.getCheckOutTime())).thenReturn(false);
        when(personelValidator.calculatePenalty(workTime)).thenReturn(penalty);

        // When
        personelService.workHoursCalculate2(testPersonel);

        // Then
        assertEquals(false, testWork.getIsWorkValid());
        assertEquals(29000.0, testPersonel.getSalary());

        verify(personelValidator).calculateWorkTime(testWork.getCheckInTime(), testWork.getCheckOutTime());
        verify(personelValidator).isWorkValid(testWork.getCheckInTime(), testWork.getCheckOutTime());
        verify(personelValidator).calculatePenalty(workTime);
    }

    @Test
    void workHoursCalculate2_WhenWorkIsInvalidButAdmin_ShouldNotApplyPenalty() {
        // Given
        Personel testPersonel = new Personel();
        testPersonel.setSalary(40000.0);
        testPersonel.setAdministrator(true);

        Work testWork = new Work();
        testWork.setCheckInTime(LocalTime.of(10, 0));
        testWork.setCheckOutTime(LocalTime.of(16, 0));
        testPersonel.setWork(testWork);

        Duration workTime = Duration.between(testWork.getCheckInTime(), testWork.getCheckOutTime());

        when(personelValidator.calculateWorkTime(testWork.getCheckInTime(), testWork.getCheckOutTime())).thenReturn(workTime);
        when(personelValidator.isWorkValid(testWork.getCheckInTime(), testWork.getCheckOutTime())).thenReturn(false);

        // When
        personelService.workHoursCalculate2(testPersonel);

        // Then
        assertEquals(false, testWork.getIsWorkValid());
        assertEquals(40000.0, testPersonel.getSalary());

        verify(personelValidator).calculateWorkTime(testWork.getCheckInTime(), testWork.getCheckOutTime());
        verify(personelValidator).isWorkValid(testWork.getCheckInTime(), testWork.getCheckOutTime());
        verify(personelValidator, never()).calculatePenalty(any(Duration.class));
    }

    @Test
    void getOneWorkofPersonel_WhenPersonelAndWorkExist_ShouldReturnWork() {
        // Given
        Long personelId = 1L;
        Long workId = 1L;

        work.setWorkId(workId);
        personel.setWork(work);

        when(personelRepository.findById(personelId)).thenReturn(Optional.of(personel));
        when(workServiceImpl.findById(workId)).thenReturn(Optional.of(work));
        when(personelMapper.DbWorktoWork(any())).thenReturn(work);

        // When
        Work result = personelService.getOneWorkofPersonel(personelId);

        // Then
        assertEquals(work, result);

        verify(personelRepository).findById(personelId);
        verify(workServiceImpl).findById(workId);
        verify(personelMapper).DbWorktoWork(any());
    }

    @Test
    void getOneWorkofPersonel_WhenPersonelDoesNotExist_ShouldInvokeRepositoryAndFail() {
        // Given
        Long personelId = 999L;
        when(personelRepository.findById(personelId)).thenReturn(Optional.empty());

        // When
        try {
            personelService.getOneWorkofPersonel(personelId);
            fail("Expected exception was not thrown");
        } catch (Exception e) {
            // Then
            verify(personelRepository).findById(personelId);
            verify(workServiceImpl, never()).findById(anyLong());
        }
    }

    @Test
    void getOneWorkofPersonel_WhenWorkDoesNotExist_ShouldInvokeRepositoryAndFail() {
        // Given
        Long personelId = 1L;
        Long workId = 1L;

        work.setWorkId(workId);
        personel.setWork(work);

        when(personelRepository.findById(personelId)).thenReturn(Optional.of(personel));
        when(workServiceImpl.findById(workId)).thenReturn(Optional.empty());

        // When
        try {
            personelService.getOneWorkofPersonel(personelId);
            fail("Expected exception was not thrown");
        } catch (Exception e) {
            // Then
            verify(personelRepository).findById(personelId);
            verify(workServiceImpl).findById(workId);

            verify(personelMapper, never()).DbWorktoWork(any());
        }
    }

    @Test
    void listSalaries_ShouldReturnSalaryMap() {
        // Given
        Map<String, Double> salaryMap = new HashMap<>();
        salaryMap.put("Test User", 30000.0);

        when(personelRepository.findAll()).thenReturn(personelList);
        when(personelMapper.personelsToSalaryMap(personelList)).thenReturn(salaryMap);

        // When
        Map<String, Double> result = personelService.listSalaries();

        // Then
        assertEquals(salaryMap, result);
        assertEquals(1, result.size());
        assertEquals(30000.0, result.get("Test User"));

        verify(personelRepository).findAll();
        verify(personelMapper).personelsToSalaryMap(personelList);
    }
}