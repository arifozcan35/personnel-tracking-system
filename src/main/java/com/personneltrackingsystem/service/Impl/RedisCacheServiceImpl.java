package com.personneltrackingsystem.service.impl;

import com.personneltrackingsystem.dto.DtoDailyPersonnelEntry;
import com.personneltrackingsystem.dto.DtoTurnstileBasedPersonnelEntry;
import com.personneltrackingsystem.service.RedisCacheService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisCacheServiceImpl implements RedisCacheService {
    
    @Qualifier("monthlyPersonnelRedisTemplate")
    private final RedisTemplate<String, HashMap<String, List<DtoDailyPersonnelEntry>>> monthlyRedisTemplate;
    
    @Qualifier("turnstileBasedMonthlyPersonnelRedisTemplate")
    private final RedisTemplate<String, HashMap<String, Map<String, List<DtoTurnstileBasedPersonnelEntry>>>> turnstileBasedMonthlyRedisTemplate;
    
    private static final String MONTHLY_PERSONNEL_KEY_PREFIX = "monthly_personnel:";
    private static final String TURNSTILE_BASED_MONTHLY_PERSONNEL_KEY_PREFIX = "turnstile_based_monthly_personnel:";
    
    private static final Duration MONTHLY_TTL = Duration.ofDays(7); // 7 days TTL
    
    // Aylık personel listesi metodları
    
    @Override
    public void cacheMonthlyPersonnelList(YearMonth yearMonth, HashMap<String, List<DtoDailyPersonnelEntry>> personnelListByDay) {
        try {
            String cacheKey = generateMonthlyKey(yearMonth);
            monthlyRedisTemplate.opsForValue().set(cacheKey, personnelListByDay, MONTHLY_TTL);
            
            log.info("Monthly personnel list cached successfully in Redis for month: {}", yearMonth);
        } catch (Exception e) {
            log.error("Error caching monthly personnel list in Redis for month: {}", yearMonth, e);
        }
    }

    @Override
    public Optional<HashMap<String, List<DtoDailyPersonnelEntry>>> getMonthlyPersonnelListFromCache(YearMonth yearMonth) {
        try {
            String cacheKey = generateMonthlyKey(yearMonth);
            HashMap<String, List<DtoDailyPersonnelEntry>> cachedMap = monthlyRedisTemplate.opsForValue().get(cacheKey);
            
            if (cachedMap != null) {
                log.info("Monthly personnel list retrieved from Redis cache for month: {}", yearMonth);
                return Optional.of(cachedMap);
            } else {
                log.info("Monthly personnel list not found in Redis cache for month: {}", yearMonth);
                return Optional.empty();
            }
        } catch (Exception e) {
            log.error("Error retrieving monthly personnel list from Redis cache for month: {}", yearMonth, e);
            return Optional.empty();
        }
    }

    @Override
    public void removeMonthlyPersonnelListFromCache(YearMonth yearMonth) {
        try {
            String cacheKey = generateMonthlyKey(yearMonth);
            monthlyRedisTemplate.delete(cacheKey);
            
            log.info("Monthly personnel list removed from Redis cache for month: {}", yearMonth);
        } catch (Exception e) {
            log.error("Error removing monthly personnel list from Redis cache for month: {}", yearMonth, e);
        }
    }

    @Override
    public void clearAllMonthlyPersonnelCache() {
        try {
            Set<String> keys = monthlyRedisTemplate.keys(MONTHLY_PERSONNEL_KEY_PREFIX + "*");
            if (keys != null && !keys.isEmpty()) {
                monthlyRedisTemplate.delete(keys);
                log.info("All monthly personnel cache cleared successfully from Redis. Cleared {} keys", keys.size());
            } else {
                log.info("No monthly personnel cache keys found in Redis to clear");
            }
        } catch (Exception e) {
            log.error("Error clearing all monthly personnel cache from Redis", e);
        }
    }
    
    private String generateMonthlyKey(YearMonth yearMonth) {
        return MONTHLY_PERSONNEL_KEY_PREFIX + yearMonth.toString();
    }
    
    // Turnike bazlı aylık personel listesi metodları
    
    @Override
    public void cacheTurnstileBasedMonthlyPersonnelList(YearMonth yearMonth, 
            HashMap<String, Map<String, List<DtoTurnstileBasedPersonnelEntry>>> personnelListByTurnstile) {
        try {
            String cacheKey = generateTurnstileBasedMonthlyKey(yearMonth);
            turnstileBasedMonthlyRedisTemplate.opsForValue().set(cacheKey, personnelListByTurnstile, MONTHLY_TTL);
            
            log.info("Turnstile-based monthly personnel list cached successfully in Redis for month: {}", yearMonth);
        } catch (Exception e) {
            log.error("Error caching turnstile-based monthly personnel list in Redis for month: {}", yearMonth, e);
        }
    }

    @Override
    public Optional<HashMap<String, Map<String, List<DtoTurnstileBasedPersonnelEntry>>>> getTurnstileBasedMonthlyPersonnelListFromCache(YearMonth yearMonth) {
        try {
            String cacheKey = generateTurnstileBasedMonthlyKey(yearMonth);
            HashMap<String, Map<String, List<DtoTurnstileBasedPersonnelEntry>>> cachedMap = 
                turnstileBasedMonthlyRedisTemplate.opsForValue().get(cacheKey);
            
            if (cachedMap != null) {
                log.info("Turnstile-based monthly personnel list retrieved from Redis cache for month: {}", yearMonth);
                return Optional.of(cachedMap);
            } else {
                log.info("Turnstile-based monthly personnel list not found in Redis cache for month: {}", yearMonth);
                return Optional.empty();
            }
        } catch (Exception e) {
            log.error("Error retrieving turnstile-based monthly personnel list from Redis cache for month: {}", yearMonth, e);
            return Optional.empty();
        }
    }

    @Override
    public void removeTurnstileBasedMonthlyPersonnelListFromCache(YearMonth yearMonth) {
        try {
            String cacheKey = generateTurnstileBasedMonthlyKey(yearMonth);
            turnstileBasedMonthlyRedisTemplate.delete(cacheKey);
            
            log.info("Turnstile-based monthly personnel list removed from Redis cache for month: {}", yearMonth);
        } catch (Exception e) {
            log.error("Error removing turnstile-based monthly personnel list from Redis cache for month: {}", yearMonth, e);
        }
    }

    @Override
    public void clearAllTurnstileBasedMonthlyPersonnelCache() {
        try {
            Set<String> keys = turnstileBasedMonthlyRedisTemplate.keys(TURNSTILE_BASED_MONTHLY_PERSONNEL_KEY_PREFIX + "*");
            if (keys != null && !keys.isEmpty()) {
                turnstileBasedMonthlyRedisTemplate.delete(keys);
                log.info("All turnstile-based monthly personnel cache cleared successfully from Redis. Cleared {} keys", keys.size());
            } else {
                log.info("No turnstile-based monthly personnel cache keys found in Redis to clear");
            }
        } catch (Exception e) {
            log.error("Error clearing all turnstile-based monthly personnel cache from Redis", e);
        }
    }
    
    private String generateTurnstileBasedMonthlyKey(YearMonth yearMonth) {
        return TURNSTILE_BASED_MONTHLY_PERSONNEL_KEY_PREFIX + yearMonth.toString();
    }
} 