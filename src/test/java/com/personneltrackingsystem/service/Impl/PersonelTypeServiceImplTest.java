package com.personneltrackingsystem.service.impl;

import com.personneltrackingsystem.dto.DtoPersonelType;
import com.personneltrackingsystem.entity.PersonelType;
import com.personneltrackingsystem.exception.BaseException;
import com.personneltrackingsystem.exception.ValidationException;
import com.personneltrackingsystem.mapper.PersonelTypeMapper;
import com.personneltrackingsystem.repository.PersonelTypeRepository;
import com.personneltrackingsystem.service.impl.PersonelTypeServiceImpl;

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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PersonelTypeServiceImplTest {

    @Mock
    private PersonelTypeRepository personelTypeRepository;

    @Mock
    private PersonelTypeMapper personelTypeMapper;

    @InjectMocks
    private PersonelTypeServiceImpl personelTypeService;

    private PersonelType personelType;
    private DtoPersonelType dtoPersonelType;
    private List<PersonelType> personelTypeList;
    private List<DtoPersonelType> dtoPersonelTypeList;

    @BeforeEach
    void setUp() {
        personelType = new PersonelType();
        personelType.setPersonelTypeId(1L);
        personelType.setPersonelTypeName("Test Tip");
        personelType.setBaseSalary(50000.0);

        dtoPersonelType = new DtoPersonelType();
        dtoPersonelType.setPersonelTypeId(1L);
        dtoPersonelType.setPersonelTypeName("Test Tip");
        dtoPersonelType.setBaseSalary(50000.0);

        personelTypeList = Arrays.asList(personelType);
        dtoPersonelTypeList = Arrays.asList(dtoPersonelType);
    }

    @Test
    void initializeDefaultPersonelTypes_ShouldCreateDefaultTypes_WhenRepositoryIsEmpty() {
        // Arrange
        when(personelTypeRepository.count()).thenReturn(0L);
        when(personelTypeRepository.saveAll(anyList())).thenReturn(Arrays.asList(
            new PersonelType(null, "Manager", 70000.0),
            new PersonelType(null, "Staff", 50000.0),
            new PersonelType(null, "Security", 40000.0),
            new PersonelType(null, "Janitor", 35000.0)
        ));

        // Act
        personelTypeService.initializeDefaultPersonelTypes();

        // Assert
        verify(personelTypeRepository, times(1)).count();
        verify(personelTypeRepository, times(1)).saveAll(anyList());
    }

    @Test
    void initializeDefaultPersonelTypes_ShouldNotCreateDefaultTypes_WhenRepositoryIsNotEmpty() {
        // Arrange
        when(personelTypeRepository.count()).thenReturn(2L);

        // Act
        personelTypeService.initializeDefaultPersonelTypes();

        // Assert
        verify(personelTypeRepository, times(1)).count();
        verify(personelTypeRepository, never()).saveAll(anyList());
    }

    @Test
    void getAllPersonelTypes_ShouldReturnAllPersonelTypes() {
        // Arrange
        when(personelTypeRepository.findAll()).thenReturn(personelTypeList);
        when(personelTypeMapper.personelTypeListToDtoPersonelTypeList(personelTypeList))
                .thenReturn(dtoPersonelTypeList);

        // Act
        List<DtoPersonelType> result = personelTypeService.getAllPersonelTypes();

        // Assert
        assertEquals(dtoPersonelTypeList, result);
        verify(personelTypeRepository, times(1)).findAll();
        verify(personelTypeMapper, times(1)).personelTypeListToDtoPersonelTypeList(personelTypeList);
    }

    @Test
    void getPersonelTypeById_ShouldReturnPersonelType_WhenIdExists() {
        // Arrange
        when(personelTypeRepository.findById(1L)).thenReturn(Optional.of(personelType));
        when(personelTypeMapper.personelTypeToDtoPersonelType(personelType)).thenReturn(dtoPersonelType);

        // Act
        Optional<DtoPersonelType> result = personelTypeService.getPersonelTypeById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(dtoPersonelType, result.get());
        verify(personelTypeRepository, times(1)).findById(1L);
        verify(personelTypeMapper, times(1)).personelTypeToDtoPersonelType(personelType);
    }

    @Test
    void getPersonelTypeById_ShouldThrowException_WhenIdDoesNotExist() {
        // Arrange
        when(personelTypeRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BaseException.class, () -> personelTypeService.getPersonelTypeById(99L));
        verify(personelTypeRepository, times(1)).findById(99L);
    }

    @Test
    void getOnePersonelType_ShouldReturnPersonelType_WhenIdExists() {
        // Arrange
        when(personelTypeRepository.findById(1L)).thenReturn(Optional.of(personelType));
        when(personelTypeMapper.personelTypeToDtoPersonelType(personelType)).thenReturn(dtoPersonelType);

        // Act
        DtoPersonelType result = personelTypeService.getOnePersonelType(1L);

        // Assert
        assertEquals(dtoPersonelType, result);
        verify(personelTypeRepository, times(1)).findById(1L);
        verify(personelTypeMapper, times(1)).personelTypeToDtoPersonelType(personelType);
    }

    @Test
    void getOnePersonelType_ShouldThrowException_WhenIdDoesNotExist() {
        // Arrange
        when(personelTypeRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BaseException.class, () -> personelTypeService.getOnePersonelType(99L));
        verify(personelTypeRepository, times(1)).findById(99L);
    }

    @Test
    void saveOnePersonelType_ShouldSaveAndReturnPersonelType_WhenValid() {
        // Arrange
        when(personelTypeRepository.existsByPersonelTypeName(anyString())).thenReturn(false);
        when(personelTypeMapper.dtoPersonelTypeToPersonelType(dtoPersonelType)).thenReturn(personelType);
        when(personelTypeRepository.save(personelType)).thenReturn(personelType);
        when(personelTypeMapper.personelTypeToDtoPersonelType(personelType)).thenReturn(dtoPersonelType);

        // Act
        DtoPersonelType result = personelTypeService.saveOnePersonelType(dtoPersonelType);

        // Assert
        assertEquals(dtoPersonelType, result);
        verify(personelTypeRepository, times(1)).existsByPersonelTypeName(dtoPersonelType.getPersonelTypeName());
        verify(personelTypeMapper, times(1)).dtoPersonelTypeToPersonelType(dtoPersonelType);
        verify(personelTypeRepository, times(1)).save(personelType);
        verify(personelTypeMapper, times(1)).personelTypeToDtoPersonelType(personelType);
    }

    @Test
    void saveOnePersonelType_ShouldThrowException_WhenNameIsEmpty() {
        // Arrange
        DtoPersonelType emptyNameDto = new DtoPersonelType();
        emptyNameDto.setPersonelTypeId(1L);
        emptyNameDto.setBaseSalary(50000.0);

        // Act & Assert
        assertThrows(ValidationException.class, () -> personelTypeService.saveOnePersonelType(emptyNameDto));
        verify(personelTypeRepository, never()).save(any());
    }

    @Test
    void saveOnePersonelType_ShouldThrowException_WhenNameAlreadyExists() {
        // Arrange
        when(personelTypeRepository.existsByPersonelTypeName(anyString())).thenReturn(true);

        // Act & Assert
        assertThrows(ValidationException.class, () -> personelTypeService.saveOnePersonelType(dtoPersonelType));
        verify(personelTypeRepository, times(1)).existsByPersonelTypeName(dtoPersonelType.getPersonelTypeName());
        verify(personelTypeRepository, never()).save(any());
    }

    @Test
    void updateOnePersonelType_ShouldUpdateAndReturnPersonelType_WhenValid() {
        // Arrange
        DtoPersonelType updatedDto = new DtoPersonelType();
        updatedDto.setPersonelTypeName("Güncellenmiş Tip");
        updatedDto.setBaseSalary(55000.0);

        PersonelType existingPersonelType = new PersonelType();
        existingPersonelType.setPersonelTypeId(1L);
        existingPersonelType.setPersonelTypeName("Test Tip");
        existingPersonelType.setBaseSalary(50000.0);

        PersonelType updatedPersonelType = new PersonelType();
        updatedPersonelType.setPersonelTypeId(1L);
        updatedPersonelType.setPersonelTypeName("Güncellenmiş Tip");
        updatedPersonelType.setBaseSalary(55000.0);

        when(personelTypeRepository.findById(1L)).thenReturn(Optional.of(existingPersonelType));
        when(personelTypeRepository.existsByPersonelTypeName(updatedDto.getPersonelTypeName())).thenReturn(false);
        when(personelTypeRepository.save(any(PersonelType.class))).thenReturn(updatedPersonelType);
        when(personelTypeMapper.personelTypeToDtoPersonelType(updatedPersonelType)).thenReturn(updatedDto);

        // Act
        DtoPersonelType result = personelTypeService.updateOnePersonelType(1L, updatedDto);

        // Assert
        assertEquals(updatedDto, result);
        verify(personelTypeRepository, times(1)).findById(1L);
        verify(personelTypeRepository, times(1)).existsByPersonelTypeName(updatedDto.getPersonelTypeName());
        verify(personelTypeRepository, times(1)).save(any(PersonelType.class));
        verify(personelTypeMapper, times(1)).personelTypeToDtoPersonelType(updatedPersonelType);
    }

    @Test
    void updateOnePersonelType_ShouldThrowException_WhenIdDoesNotExist() {
        // Arrange
        when(personelTypeRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BaseException.class, () -> personelTypeService.updateOnePersonelType(99L, dtoPersonelType));
        verify(personelTypeRepository, times(1)).findById(99L);
        verify(personelTypeRepository, never()).save(any());
    }

    @Test
    void updateOnePersonelType_ShouldThrowException_WhenNewNameAlreadyExists() {
        // Arrange
        DtoPersonelType updatedDto = new DtoPersonelType();
        updatedDto.setPersonelTypeName("Var Olan İsim");
        
        PersonelType existingPersonelType = new PersonelType();
        existingPersonelType.setPersonelTypeId(1L);
        existingPersonelType.setPersonelTypeName("Test Tip");
        
        when(personelTypeRepository.findById(1L)).thenReturn(Optional.of(existingPersonelType));
        when(personelTypeRepository.existsByPersonelTypeName("Var Olan İsim")).thenReturn(true);

        // Act & Assert
        assertThrows(ValidationException.class, () -> personelTypeService.updateOnePersonelType(1L, updatedDto));
        verify(personelTypeRepository, times(1)).findById(1L);
        verify(personelTypeRepository, times(1)).existsByPersonelTypeName("Var Olan İsim");
        verify(personelTypeRepository, never()).save(any());
    }

    @Test
    void deleteOnePersonelType_ShouldDeletePersonelType_WhenIdExists() {
        // Arrange
        when(personelTypeRepository.findById(1L)).thenReturn(Optional.of(personelType));
        doNothing().when(personelTypeRepository).delete(personelType);

        // Act
        personelTypeService.deleteOnePersonelType(1L);

        // Assert
        verify(personelTypeRepository, times(1)).findById(1L);
        verify(personelTypeRepository, times(1)).delete(personelType);
    }

    @Test
    void deleteOnePersonelType_ShouldThrowException_WhenIdDoesNotExist() {
        // Arrange
        when(personelTypeRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BaseException.class, () -> personelTypeService.deleteOnePersonelType(99L));
        verify(personelTypeRepository, times(1)).findById(99L);
        verify(personelTypeRepository, never()).delete(any());
    }
} 