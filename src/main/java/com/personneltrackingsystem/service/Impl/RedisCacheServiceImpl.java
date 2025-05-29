package com.personneltrackingsystem.service.Impl;

import com.personneltrackingsystem.dto.DtoDailyPersonnelEntry;
import com.personneltrackingsystem.service.RedisCacheService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisCacheServiceImpl implements RedisCacheService {

    @Qualifier("dailyPersonnelRedisTemplate")
    private final RedisTemplate<String, List<DtoDailyPersonnelEntry>> redisTemplate;
    
    private static final String DAILY_PERSONNEL_KEY_PREFIX = "daily_personnel:";
    
    private static final Duration TTL = Duration.ofDays(1); // 24 hours TTL


    @Override
    public void cacheDailyPersonnelList(LocalDate date, List<DtoDailyPersonnelEntry> personnelList) {
        try {
            String cacheKey = generateCacheKey(date);
            redisTemplate.opsForValue().set(cacheKey, personnelList, TTL);
            
            log.info("Daily personnel list cached successfully in Redis for date: {}", date);
        } catch (Exception e) {
            log.error("Error caching daily personnel list in Redis for date: {}", date, e);
        }
    }


    @Override
    public Optional<List<DtoDailyPersonnelEntry>> getDailyPersonnelListFromCache(LocalDate date) {
        try {
            String cacheKey = generateCacheKey(date);
            List<DtoDailyPersonnelEntry> cachedList = redisTemplate.opsForValue().get(cacheKey);
            
            if (cachedList != null) {
                log.info("Daily personnel list retrieved from Redis cache for date: {}", date);
                return Optional.of(cachedList);
            } else {
                log.info("Daily personnel list not found in Redis cache for date: {}", date);
                return Optional.empty();
            }
        } catch (Exception e) {
            log.error("Error retrieving daily personnel list from Redis cache for date: {}", date, e);
            return Optional.empty();
        }
    }


    @Override
    public void removeDailyPersonnelListFromCache(LocalDate date) {
        try {
            String cacheKey = generateCacheKey(date);
            redisTemplate.delete(cacheKey);
            
            log.info("Daily personnel list removed from Redis cache for date: {}", date);
        } catch (Exception e) {
            log.error("Error removing daily personnel list from Redis cache for date: {}", date, e);
        }
    }

    
    @Override
    public void clearAllDailyPersonnelCache() {
        try {
            Set<String> keys = redisTemplate.keys(DAILY_PERSONNEL_KEY_PREFIX + "*");
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
                log.info("All daily personnel cache cleared successfully from Redis. Cleared {} keys", keys.size());
            } else {
                log.info("No daily personnel cache keys found in Redis to clear");
            }
        } catch (Exception e) {
            log.error("Error clearing all daily personnel cache from Redis", e);
        }
    }

    private String generateCacheKey(LocalDate date) {
        return DAILY_PERSONNEL_KEY_PREFIX + date.toString();
    }
} 