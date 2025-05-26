package com.personneltrackingsystem.service;

import com.personneltrackingsystem.entity.Personel;

import java.util.Optional;

public interface PersonelCacheService {
    
    void cachePersonel(Long personelId, Personel personel);
    
    Optional<Personel> getPersonelFromCache(Long personelId);
    
    void removePersonelFromCache(Long personelId);
    
    boolean isPersonelCached(Long personelId);
} 