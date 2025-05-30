package com.personneltrackingsystem.service.impl;

import com.personneltrackingsystem.entity.Personel;
import com.personneltrackingsystem.service.PersonelCacheService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class PersonelCacheServiceImpl implements PersonelCacheService {

    private final RedisTemplate<String, Personel> redisTemplate;
    
    private static final String CACHE_KEY_PREFIX = "personnel:";
    
    private static final long CACHE_TTL_HOURS = 1;


    @Override
    public void cachePersonel(Long personelId, Personel personel) {
        try {
            String key = CACHE_KEY_PREFIX + personelId;
            redisTemplate.opsForValue().set(key, personel, CACHE_TTL_HOURS, TimeUnit.HOURS);
            log.info("Personnel cached successfully with ID: {}", personelId);
        } catch (Exception e) {
            log.error("Error caching personnel with ID: {}", personelId, e);
        }
    }


    @Override
    public Optional<Personel> getPersonelFromCache(Long personelId) {
        try {
            String key = CACHE_KEY_PREFIX + personelId;
            Personel personel = redisTemplate.opsForValue().get(key);
            if (personel != null) {
                log.info("Personnel found in cache with ID: {}", personelId);
                return Optional.of(personel);
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

    
    @Override
    public boolean isPersonelCached(Long personelId) {
        try {
            String key = CACHE_KEY_PREFIX + personelId;
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            log.error("Error checking if personnel is cached with ID: {}", personelId, e);
            return false;
        }
    }
} 