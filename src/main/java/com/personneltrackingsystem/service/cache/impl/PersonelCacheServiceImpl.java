package com.personneltrackingsystem.service.cache.impl;

import com.personneltrackingsystem.dto.DtoPersonelCache;
import com.personneltrackingsystem.dto.DtoPersonelCache.DtoUnitCache;
import com.personneltrackingsystem.entity.Personel;
import com.personneltrackingsystem.entity.Unit;
import com.personneltrackingsystem.service.cache.PersonelCacheService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class PersonelCacheServiceImpl implements PersonelCacheService {

    private final RedisTemplate<String, DtoPersonelCache> redisTemplate;
    
    private static final String CACHE_KEY_PREFIX = "personnel:";
    
    private static final long CACHE_TTL_HOURS = 1;


    @Override
    public void cachePersonel(Long personelId, Personel personel) {
        try {
            String key = CACHE_KEY_PREFIX + personelId;
            DtoPersonelCache dto = convertToDto(personel);
            redisTemplate.opsForValue().set(key, dto, CACHE_TTL_HOURS, TimeUnit.HOURS);
            log.info("Personnel cached successfully with ID: {}", personelId);
        } catch (Exception e) {
            log.error("Error caching personnel with ID: {}", personelId, e);
        }
    }


    @Override
    public Optional<Personel> getPersonelFromCache(Long personelId) {
        try {
            String key = CACHE_KEY_PREFIX + personelId;
            DtoPersonelCache dto = redisTemplate.opsForValue().get(key);
            if (dto != null) {
                log.info("Personnel found in cache with ID: {}", personelId);
                return Optional.of(convertToEntity(dto));
            } else {
                log.info("Personnel not found in cache with ID: {}", personelId);
                return Optional.empty();
            }
        } catch (Exception e) {
            log.error("Error retrieving personnel from cache with ID: {}", personelId, e);
            return Optional.empty();
        }
    }


    @Override
    public void removePersonelFromCache(Long personelId) {
        try {
            String key = CACHE_KEY_PREFIX + personelId;
            redisTemplate.delete(key);
            log.info("Personnel removed from cache with ID: {}", personelId);
        } catch (Exception e) {
            log.error("Error removing personnel from cache with ID: {}", personelId, e);
        }
    }

    
    
    // Helper method
    private DtoPersonelCache convertToDto(Personel personel) {
        if (personel == null) return null;
        
        DtoPersonelCache dto = new DtoPersonelCache();
        dto.setPersonelId(personel.getPersonelId());
        dto.setName(personel.getName());
        dto.setEmail(personel.getEmail());
        
        try {
            if (personel.getPersonelTypeId() != null) {
                dto.setPersonelTypeId(personel.getPersonelTypeId().getPersonelTypeId());
                dto.setPersonelTypeName(personel.getPersonelTypeId().getPersonelTypeName());
            }
            
            if (personel.getUnitId() != null) {
                List<DtoUnitCache> unitDtos = new ArrayList<>();
                
                for (Unit unit : personel.getUnitId()) {
                    if (unit != null) {
                        DtoUnitCache unitDto = new DtoUnitCache();
                        unitDto.setUnitId(unit.getUnitId());
                        unitDto.setUnitName(unit.getUnitName());
                        unitDtos.add(unitDto);
                    }
                }
                
                dto.setUnits(unitDtos);
            }
        } catch (Exception e) {
            log.error("Error while converting Personel to DTO: {}", e.getMessage());
        }
        
        return dto;
    }
    
    // Helper method
    private Personel convertToEntity(DtoPersonelCache dto) {
        if (dto == null) return null;
        
        Personel personel = new Personel();
        personel.setPersonelId(dto.getPersonelId());
        personel.setName(dto.getName());
        personel.setEmail(dto.getEmail());
        
        return personel;
    }
} 