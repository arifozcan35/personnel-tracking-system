package com.personneltrackingsystem.service.impl;

import com.personneltrackingsystem.dto.*;
import com.personneltrackingsystem.entity.Personel;
import com.personneltrackingsystem.entity.PersonelType;
import com.personneltrackingsystem.entity.Unit;
import com.personneltrackingsystem.exception.BaseException;
import com.personneltrackingsystem.exception.ValidationException;
import com.personneltrackingsystem.mapper.PersonelMapper;
import com.personneltrackingsystem.repository.PersonelRepository;
import com.personneltrackingsystem.service.PersonelCacheService;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonelServiceImplTest {

    @Mock
    private PersonelRepository personelRepository;

    @Mock
    private PersonelMapper personelMapper;

    @Mock
    private PersonelCacheService personelCacheService;

    @InjectMocks
    private PersonelServiceImpl personelService;

    private Personel personel;
    private DtoPersonel dtoPersonel;
    private DtoPersonelAll dtoPersonelAll;
    private DtoPersonelIU dtoPersonelIU;
    private List<Personel> personelList;
    private List<DtoPersonel> dtoPersonelList;
    private List<Unit> unitList;

    @BeforeEach
    void setUp() {
        // Test verileri oluşturma
        personel = new Personel();
        personel.setPersonelId(1L);
        personel.setName("Test Personel");
        personel.setEmail("test@test.com");

        PersonelType personelType = new PersonelType();
        personelType.setPersonelTypeId(1L);
        personelType.setPersonelTypeName("Test Tip");
        personel.setPersonelTypeId(personelType);

        Unit unit = new Unit();
        unit.setUnitId(1L);
        unit.setUnitName("Test Birim");

        unitList = new ArrayList<>();
        unitList.add(unit);
        personel.setUnitId(unitList);

        // DTO nesneleri
        dtoPersonel = new DtoPersonel();
        dtoPersonel.setName("Test Personel");
        dtoPersonel.setEmail("test@test.com");

        dtoPersonelAll = new DtoPersonelAll();

        dtoPersonelIU = new DtoPersonelIU();
        dtoPersonelIU.setName("Test Personel");
        dtoPersonelIU.setEmail("test@test.com");
        dtoPersonelIU.setPersonelTypeId(1L);
        dtoPersonelIU.setUnitId(Collections.singletonList(1L));

        personelList = new ArrayList<>();
        personelList.add(personel);

        dtoPersonelList = new ArrayList<>();
        dtoPersonelList.add(dtoPersonel);
    }

    @Test
    void checkIfPersonelExists_PersonelExists_ReturnsPersonel() {
        // Arrange
        when(personelRepository.findById(anyLong())).thenReturn(Optional.of(personel));

        // Act
        Personel result = personelService.checkIfPersonelExists(1L);

        // Assert
        assertNotNull(result);
        assertEquals(personel.getPersonelId(), result.getPersonelId());
        verify(personelRepository).findById(1L);
    }

    @Test
    void checkIfPersonelExists_PersonelNotExists_ThrowsBaseException() {
        // Arrange
        when(personelRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BaseException.class, () -> personelService.checkIfPersonelExists(1L));
        verify(personelRepository).findById(1L);
    }

    @Test
    void getAllPersonels_ReturnsAllPersonels() {
        // Arrange
        when(personelRepository.findAll()).thenReturn(personelList);
        when(personelMapper.personelsToDtoPersonels(personelList)).thenReturn(dtoPersonelList);

        // Act
        List<DtoPersonel> result = personelService.getAllPersonels();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(dtoPersonel.getName(), result.get(0).getName());
        verify(personelRepository).findAll();
        verify(personelMapper).personelsToDtoPersonels(personelList);
    }

    @Test
    void getAOnePersonel_PersonelExists_ReturnsDtoPersonelAll() {
        // Arrange
        when(personelRepository.findById(anyLong())).thenReturn(Optional.of(personel));
        when(personelMapper.personelToDtoPersonelAll(any(Personel.class))).thenReturn(dtoPersonelAll);

        // Act
        DtoPersonelAll result = personelService.getAOnePersonel(1L);

        // Assert
        assertNotNull(result);
        verify(personelRepository).findById(1L);
        verify(personelMapper).personelToDtoPersonelAll(personel);
    }

    @Test
    void getAOnePersonel_PersonelNotExists_ThrowsBaseException() {
        // Arrange
        when(personelRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BaseException.class, () -> personelService.getAOnePersonel(1L));
        verify(personelRepository).findById(1L);
    }

    @Test
    void saveOnePersonel_ValidPersonel_ReturnCreated() {
        // Arrange
        when(personelRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(personelMapper.dtoPersonelIUToPersonel(any(DtoPersonelIU.class))).thenReturn(personel);
        when(personelRepository.save(any(Personel.class))).thenReturn(personel);

        // Act
        ResponseEntity<String> result = personelService.saveOnePersonel(dtoPersonelIU);

        // Assert
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertTrue(result.getBody().contains("Personnel registered successfully"));
        verify(personelRepository).findByEmail(dtoPersonelIU.getEmail());
        verify(personelMapper).dtoPersonelIUToPersonel(dtoPersonelIU);
        verify(personelRepository).save(any(Personel.class));
    }

    @Test
    void saveOnePersonel_EmailAlreadyExists_ThrowsValidationException() {
        // Arrange
        when(personelRepository.findByEmail(anyString())).thenReturn(Optional.of(personel));

        // Act & Assert
        assertThrows(ValidationException.class, () -> personelService.saveOnePersonel(dtoPersonelIU));
        verify(personelRepository).findByEmail(dtoPersonelIU.getEmail());
        verify(personelRepository, never()).save(any(Personel.class));
    }

    @Test
    void saveOnePersonel_MissingName_ThrowsValidationException() {
        // Arrange
        dtoPersonelIU.setName(null);

        // Act & Assert
        assertThrows(ValidationException.class, () -> personelService.saveOnePersonel(dtoPersonelIU));
        verify(personelRepository, never()).save(any(Personel.class));
    }

    @Test
    void saveOnePersonel_MissingEmail_ThrowsValidationException() {
        // Arrange
        dtoPersonelIU.setEmail(null);

        // Act & Assert
        assertThrows(ValidationException.class, () -> personelService.saveOnePersonel(dtoPersonelIU));
        verify(personelRepository, never()).save(any(Personel.class));
    }

    @Test
    void updateOnePersonel_ValidUpdate_ReturnsOk() {
        // Arrange
        when(personelRepository.findById(anyLong())).thenReturn(Optional.of(personel));
        when(personelRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(personelMapper.longToPersonelTypeEntity(anyLong())).thenReturn(personel.getPersonelTypeId());
        when(personelMapper.longListToUnitEntityList(any())).thenReturn(personel.getUnitId());
        when(personelRepository.save(any(Personel.class))).thenReturn(personel);

        // Act
        ResponseEntity<String> result = personelService.updateOnePersonel(1L, dtoPersonelIU);

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(result.getBody().contains("Personnel updated successfully"));
        verify(personelRepository).findById(1L);
        verify(personelRepository).findByEmail(dtoPersonelIU.getEmail());
        verify(personelRepository).save(personel);
        verify(personelCacheService).removePersonelFromCache(1L);
    }

    @Test
    void updateOnePersonel_PersonelNotExists_ThrowsBaseException() {
        // Arrange
        when(personelRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BaseException.class, () -> personelService.updateOnePersonel(1L, dtoPersonelIU));
        verify(personelRepository).findById(1L);
        verify(personelRepository, never()).save(any(Personel.class));
    }

    @Test
    void updateOnePersonel_EmailAlreadyExists_ThrowsValidationException() {
        // Arrange
        Personel otherPersonel = new Personel();
        otherPersonel.setPersonelId(2L);
        otherPersonel.setEmail("test@test.com");

        when(personelRepository.findById(anyLong())).thenReturn(Optional.of(personel));
        when(personelRepository.findByEmail(anyString())).thenReturn(Optional.of(otherPersonel));

        // Act & Assert
        assertThrows(ValidationException.class, () -> personelService.updateOnePersonel(1L, dtoPersonelIU));
        verify(personelRepository).findById(1L);
        verify(personelRepository).findByEmail(dtoPersonelIU.getEmail());
        verify(personelRepository, never()).save(any(Personel.class));
    }

    @Test
    void deleteOnePersonel_PersonelExists_DeletesSuccessfully() {
        // Arrange
        when(personelRepository.findById(anyLong())).thenReturn(Optional.of(personel));
        doNothing().when(personelRepository).deleteById(anyLong());
        doNothing().when(personelCacheService).removePersonelFromCache(anyLong());

        // Act
        personelService.deleteOnePersonel(1L);

        // Assert
        verify(personelRepository).findById(1L);
        verify(personelRepository).deleteById(1L);
        verify(personelCacheService).removePersonelFromCache(1L);
    }

    @Test
    void deleteOnePersonel_PersonelNotExists_ThrowsBaseException() {
        // Arrange
        when(personelRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BaseException.class, () -> personelService.deleteOnePersonel(1L));
        verify(personelRepository).findById(1L);
        verify(personelRepository, never()).deleteById(anyLong());
    }

    @Test
    void getPersonelsByUnitId_PersonelsExist_ReturnsSet() {
        // Arrange
        when(personelRepository.findAll()).thenReturn(personelList);
        when(personelMapper.personelToDtoPersonel(any(Personel.class))).thenReturn(dtoPersonel);

        // Act
        Set<DtoPersonel> result = personelService.getPersonelsByUnitId(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(personelRepository).findAll();
        verify(personelMapper).personelToDtoPersonel(personel);
    }

    @Test
    void getPersonelsByUnitId_NoPersonelsForUnit_ThrowsBaseException() {
        // Arrange
        Unit unit = new Unit();
        unit.setUnitId(2L); // Farklı birim ID'si
        personel.setUnitId(Collections.singletonList(unit));
        
        when(personelRepository.findAll()).thenReturn(personelList);

        // Act & Assert
        assertThrows(BaseException.class, () -> personelService.getPersonelsByUnitId(1L));
        verify(personelRepository).findAll();
    }

    @Test
    void getPersonelWithCache_PersonelInCache_ReturnsFromCache() {
        // Arrange
        when(personelCacheService.getPersonelFromCache(anyLong())).thenReturn(Optional.of(personel));

        // Act
        Personel result = personelService.getPersonelWithCache(1L);

        // Assert
        assertNotNull(result);
        assertEquals(personel.getPersonelId(), result.getPersonelId());
        verify(personelCacheService).getPersonelFromCache(1L);
        verify(personelRepository, never()).findByIdWithRelationships(anyLong());
        verify(personelCacheService, never()).cachePersonel(anyLong(), any(Personel.class));
    }

    @Test
    void getPersonelWithCache_PersonelNotInCache_RetrievesFromDbAndCaches() {
        // Arrange
        when(personelCacheService.getPersonelFromCache(anyLong())).thenReturn(Optional.empty());
        when(personelRepository.findByIdWithRelationships(anyLong())).thenReturn(Optional.of(personel));
        doNothing().when(personelCacheService).cachePersonel(anyLong(), any(Personel.class));

        // Act
        Personel result = personelService.getPersonelWithCache(1L);

        // Assert
        assertNotNull(result);
        assertEquals(personel.getPersonelId(), result.getPersonelId());
        verify(personelCacheService).getPersonelFromCache(1L);
        verify(personelRepository).findByIdWithRelationships(1L);
        verify(personelCacheService).cachePersonel(eq(1L), any(Personel.class));
    }

    @Test
    void getPersonelWithCache_PersonelNotInDbOrCache_ThrowsBaseException() {
        // Arrange
        when(personelCacheService.getPersonelFromCache(anyLong())).thenReturn(Optional.empty());
        when(personelRepository.findByIdWithRelationships(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BaseException.class, () -> personelService.getPersonelWithCache(1L));
        verify(personelCacheService).getPersonelFromCache(1L);
        verify(personelRepository).findByIdWithRelationships(1L);
        verify(personelCacheService, never()).cachePersonel(anyLong(), any(Personel.class));
    }
} 