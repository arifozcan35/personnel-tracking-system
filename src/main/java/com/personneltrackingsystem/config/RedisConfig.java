package com.personneltrackingsystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.personneltrackingsystem.entity.Personel;
import com.personneltrackingsystem.dto.DtoPersonelCache;
import com.personneltrackingsystem.dto.DtoDailyPersonnelEntry;
import com.personneltrackingsystem.dto.DtoTurnstileBasedPersonnelEntry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, DtoPersonelCache> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, DtoPersonelCache> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringRedisSerializer);
        template.setHashKeySerializer(stringRedisSerializer);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        Jackson2JsonRedisSerializer<DtoPersonelCache> jsonRedisSerializer = 
            new Jackson2JsonRedisSerializer<>(objectMapper, DtoPersonelCache.class);
        
        template.setValueSerializer(jsonRedisSerializer);
        template.setHashValueSerializer(jsonRedisSerializer);

        template.afterPropertiesSet();
        return template;
    }
    
    @Bean
    public RedisTemplate<String, HashMap<String, List<DtoDailyPersonnelEntry>>> monthlyPersonnelRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, HashMap<String, List<DtoDailyPersonnelEntry>>> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Configure serializers
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringRedisSerializer);
        template.setHashKeySerializer(stringRedisSerializer);

        // Create ObjectMapper with JSR310 module for proper date/time serialization
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        
        // Configure JSON serialization for HashMap<String, List<DtoDailyPersonnelEntry>>
        Jackson2JsonRedisSerializer<HashMap> jsonRedisSerializer = 
            new Jackson2JsonRedisSerializer<>(objectMapper, HashMap.class);
        
        template.setValueSerializer(jsonRedisSerializer);
        template.setHashValueSerializer(jsonRedisSerializer);

        template.afterPropertiesSet();
        return template;
    }
    
    @Bean
    public RedisTemplate<String, HashMap<String, Map<String, List<DtoTurnstileBasedPersonnelEntry>>>> turnstileBasedMonthlyPersonnelRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, HashMap<String, Map<String, List<DtoTurnstileBasedPersonnelEntry>>>> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Configure serializers
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringRedisSerializer);
        template.setHashKeySerializer(stringRedisSerializer);

        // Create ObjectMapper with JSR310 module for proper date/time serialization
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        
        // Configure JSON serialization for HashMap<String, Map<String, List<DtoTurnstileBasedPersonnelEntry>>>
        Jackson2JsonRedisSerializer<HashMap> jsonRedisSerializer = 
            new Jackson2JsonRedisSerializer<>(objectMapper, HashMap.class);
        
        template.setValueSerializer(jsonRedisSerializer);
        template.setHashValueSerializer(jsonRedisSerializer);

        template.afterPropertiesSet();
        return template;
    }
    
    @Bean
    public RedisTemplate<String, Map<String, Map<String, List<DtoTurnstileBasedPersonnelEntry>>>> dailyTurnstilePassageRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Map<String, Map<String, List<DtoTurnstileBasedPersonnelEntry>>>> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Configure serializers
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringRedisSerializer);
        template.setHashKeySerializer(stringRedisSerializer);

        // Create ObjectMapper with JSR310 module for proper date/time serialization
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        
        // Configure JSON serialization for Map<String, Map<String, List<DtoTurnstileBasedPersonnelEntry>>>
        // (date -> (turnstile name -> list of personnel entries))
        Jackson2JsonRedisSerializer<Map> jsonRedisSerializer = 
            new Jackson2JsonRedisSerializer<>(objectMapper, Map.class);
        
        template.setValueSerializer(jsonRedisSerializer);
        template.setHashValueSerializer(jsonRedisSerializer);

        template.afterPropertiesSet();
        return template;
    }
} 