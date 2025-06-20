package com.personneltrackingsystem.service.impl;

import com.personneltrackingsystem.dto.DtoWorkingHours;
import com.personneltrackingsystem.entity.PersonelType;
import com.personneltrackingsystem.entity.WorkingHours;
import com.personneltrackingsystem.exception.BaseException;
import com.personneltrackingsystem.exception.ValidationException;
import com.personneltrackingsystem.mapper.WorkingHoursMapper;
import com.personneltrackingsystem.repository.WorkingHoursRepository;
import com.personneltrackingsystem.service.impl.WorkingHoursServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkingHoursServiceImplTest {

    @Mock
    private WorkingHoursRepository workingHoursRepository;

    @Mock
    private WorkingHoursMapper workingHoursMapper;

    @InjectMocks
    private WorkingHoursServiceImpl workingHoursService;

    private WorkingHours workingHours;
    private DtoWorkingHours dtoWorkingHours;
    private PersonelType personelType;

    @BeforeEach
    void setUp() {
        personelType = new PersonelType();
        personelType.setPersonelTypeId(1L);

        workingHours = new WorkingHours();
        workingHours.setWorkingHoursId(1L);
        workingHours.setCheckInTime(LocalTime.of(9, 0));
        workingHours.setCheckOutTime(LocalTime.of(18, 0));
        workingHours.setPersonelTypeId(personelType);

        dtoWorkingHours = new DtoWorkingHours();
        dtoWorkingHours.setCheckInTime(LocalTime.of(9, 0));
        dtoWorkingHours.setCheckOutTime(LocalTime.of(18, 0));
        dtoWorkingHours.setPersonelTypeId(1L);
    }

    @Test
    void getAllWorkingHours_ShouldReturnAllWorkingHours() {
        // Given
        List<WorkingHours> workingHoursList = Arrays.asList(workingHours);
        List<DtoWorkingHours> dtoWorkingHoursList = Arrays.asList(dtoWorkingHours);

        when(workingHoursRepository.findAll()).thenReturn(workingHoursList);
        when(workingHoursMapper.workingHoursListToDtoWorkingHoursList(workingHoursList)).thenReturn(dtoWorkingHoursList);

        // When
        List<DtoWorkingHours> result = workingHoursService.getAllWorkingHours();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(workingHoursRepository, times(1)).findAll();
        verify(workingHoursMapper, times(1)).workingHoursListToDtoWorkingHoursList(workingHoursList);
    }

    @Test
    void getWorkingHoursById_WithValidId_ShouldReturnWorkingHours() {
        // Given
        Long id = 1L;
        
        when(workingHoursRepository.findById(id)).thenReturn(Optional.of(workingHours));
        when(workingHoursMapper.workingHoursToDtoWorkingHours(workingHours)).thenReturn(dtoWorkingHours);

        // When
        Optional<DtoWorkingHours> result = workingHoursService.getWorkingHoursById(id);

        // Then
        assertTrue(result.isPresent());
        assertEquals(dtoWorkingHours, result.get());
        verify(workingHoursRepository, times(1)).findById(id);
        verify(workingHoursMapper, times(1)).workingHoursToDtoWorkingHours(workingHours);
    }

    @Test
    void getWorkingHoursById_WithInvalidId_ShouldThrowException() {
        // Given
        Long id = 999L;
        
        when(workingHoursRepository.findById(id)).thenReturn(Optional.empty());

        // When, Then
        assertThrows(BaseException.class, () -> workingHoursService.getWorkingHoursById(id));
        verify(workingHoursRepository, times(1)).findById(id);
    }

    @Test
    void getOneWorkingHours_WithValidId_ShouldReturnWorkingHours() {
        // Given
        Long id = 1L;
        
        when(workingHoursRepository.findById(id)).thenReturn(Optional.of(workingHours));
        when(workingHoursMapper.workingHoursToDtoWorkingHours(workingHours)).thenReturn(dtoWorkingHours);

        // When
        DtoWorkingHours result = workingHoursService.getOneWorkingHours(id);

        // Then
        assertNotNull(result);
        assertEquals(dtoWorkingHours, result);
        verify(workingHoursRepository, times(1)).findById(id);
        verify(workingHoursMapper, times(1)).workingHoursToDtoWorkingHours(workingHours);
    }

    @Test
    void getOneWorkingHours_WithInvalidId_ShouldThrowException() {
        // Given
        Long id = 999L;
        
        when(workingHoursRepository.findById(id)).thenReturn(Optional.empty());

        // When, Then
        assertThrows(BaseException.class, () -> workingHoursService.getOneWorkingHours(id));
        verify(workingHoursRepository, times(1)).findById(id);
    }

    @Test
    void saveOneWorkingHours_WithValidData_ShouldSaveAndReturnWorkingHours() {
        // Given
        when(workingHoursMapper.dtoWorkingHoursToWorkingHours(dtoWorkingHours)).thenReturn(workingHours);
        when(workingHoursRepository.save(workingHours)).thenReturn(workingHours);
        when(workingHoursMapper.workingHoursToDtoWorkingHours(workingHours)).thenReturn(dtoWorkingHours);

        // When
        DtoWorkingHours result = workingHoursService.saveOneWorkingHours(dtoWorkingHours);

        // Then
        assertNotNull(result);
        assertEquals(dtoWorkingHours, result);
        verify(workingHoursMapper, times(1)).dtoWorkingHoursToWorkingHours(dtoWorkingHours);
        verify(workingHoursRepository, times(1)).save(workingHours);
        verify(workingHoursMapper, times(1)).workingHoursToDtoWorkingHours(workingHours);
    }

    @Test
    void saveOneWorkingHours_WithNullCheckInTime_ShouldThrowValidationException() {
        // Given
        dtoWorkingHours.setCheckInTime(null);

        // When, Then
        assertThrows(ValidationException.class, () -> workingHoursService.saveOneWorkingHours(dtoWorkingHours));
        verify(workingHoursRepository, never()).save(any());
    }

    @Test
    void saveOneWorkingHours_WithNullCheckOutTime_ShouldThrowValidationException() {
        // Given
        dtoWorkingHours.setCheckOutTime(null);

        // When, Then
        assertThrows(ValidationException.class, () -> workingHoursService.saveOneWorkingHours(dtoWorkingHours));
        verify(workingHoursRepository, never()).save(any());
    }

    @Test
    void saveOneWorkingHours_WithNullPersonelTypeId_ShouldThrowValidationException() {
        // Given
        dtoWorkingHours.setPersonelTypeId(null);

        // When, Then
        assertThrows(ValidationException.class, () -> workingHoursService.saveOneWorkingHours(dtoWorkingHours));
        verify(workingHoursRepository, never()).save(any());
    }

    @Test
    void saveOneWorkingHours_WithInvalidTimeRange_ShouldThrowValidationException() {
        // Given
        dtoWorkingHours.setCheckInTime(LocalTime.of(18, 0));
        dtoWorkingHours.setCheckOutTime(LocalTime.of(9, 0));

        // When, Then
        assertThrows(ValidationException.class, () -> workingHoursService.saveOneWorkingHours(dtoWorkingHours));
        verify(workingHoursRepository, never()).save(any());
    }

    @Test
    void updateOneWorkingHours_WithValidData_ShouldUpdateAndReturnWorkingHours() {
        // Given
        Long id = 1L;
        DtoWorkingHours newWorkingHours = new DtoWorkingHours();
        newWorkingHours.setCheckInTime(LocalTime.of(8, 0));
        newWorkingHours.setCheckOutTime(LocalTime.of(17, 0));
        
        WorkingHours existingWorkingHours = new WorkingHours();
        existingWorkingHours.setWorkingHoursId(id);
        existingWorkingHours.setCheckInTime(LocalTime.of(9, 0));
        existingWorkingHours.setCheckOutTime(LocalTime.of(18, 0));
        existingWorkingHours.setPersonelTypeId(personelType);

        when(workingHoursRepository.findById(id)).thenReturn(Optional.of(existingWorkingHours));
        when(workingHoursRepository.save(existingWorkingHours)).thenReturn(existingWorkingHours);
        when(workingHoursMapper.workingHoursToDtoWorkingHours(existingWorkingHours)).thenReturn(newWorkingHours);

        // When
        DtoWorkingHours result = workingHoursService.updateOneWorkingHours(id, newWorkingHours);

        // Then
        assertNotNull(result);
        assertEquals(newWorkingHours, result);
        assertEquals(LocalTime.of(8, 0), existingWorkingHours.getCheckInTime());
        assertEquals(LocalTime.of(17, 0), existingWorkingHours.getCheckOutTime());
        verify(workingHoursRepository, times(1)).findById(id);
        verify(workingHoursRepository, times(1)).save(existingWorkingHours);
        verify(workingHoursMapper, times(1)).workingHoursToDtoWorkingHours(existingWorkingHours);
    }

    @Test
    void updateOneWorkingHours_WithInvalidId_ShouldThrowException() {
        // Given
        Long id = 999L;
        DtoWorkingHours newWorkingHours = new DtoWorkingHours();
        
        when(workingHoursRepository.findById(id)).thenReturn(Optional.empty());

        // When, Then
        assertThrows(BaseException.class, () -> workingHoursService.updateOneWorkingHours(id, newWorkingHours));
        verify(workingHoursRepository, times(1)).findById(id);
        verify(workingHoursRepository, never()).save(any());
    }

    @Test
    void updateOneWorkingHours_WithInvalidTimeRange_ShouldThrowValidationException() {
        // Given
        Long id = 1L;
        DtoWorkingHours newWorkingHours = new DtoWorkingHours();
        newWorkingHours.setCheckInTime(LocalTime.of(18, 0));
        newWorkingHours.setCheckOutTime(LocalTime.of(9, 0));
        
        WorkingHours existingWorkingHours = new WorkingHours();
        existingWorkingHours.setWorkingHoursId(id);
        existingWorkingHours.setCheckInTime(LocalTime.of(9, 0));
        existingWorkingHours.setCheckOutTime(LocalTime.of(18, 0));
        
        when(workingHoursRepository.findById(id)).thenReturn(Optional.of(existingWorkingHours));

        // When, Then
        assertThrows(ValidationException.class, () -> workingHoursService.updateOneWorkingHours(id, newWorkingHours));
        verify(workingHoursRepository, times(1)).findById(id);
        verify(workingHoursRepository, never()).save(any());
    }

    @Test
    void deleteOneWorkingHours_WithValidId_ShouldDeleteWorkingHours() {
        // Given
        Long id = 1L;
        
        when(workingHoursRepository.findById(id)).thenReturn(Optional.of(workingHours));
        
        // When
        workingHoursService.deleteOneWorkingHours(id);
        
        // Then
        verify(workingHoursRepository, times(1)).findById(id);
        verify(workingHoursRepository, times(1)).delete(workingHours);
    }

    @Test
    void deleteOneWorkingHours_WithInvalidId_ShouldThrowException() {
        // Given
        Long id = 999L;
        
        when(workingHoursRepository.findById(id)).thenReturn(Optional.empty());
        
        // When, Then
        assertThrows(BaseException.class, () -> workingHoursService.deleteOneWorkingHours(id));
        verify(workingHoursRepository, times(1)).findById(id);
        verify(workingHoursRepository, never()).delete(any());
    }
} 