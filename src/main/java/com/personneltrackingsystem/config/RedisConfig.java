package com.personneltrackingsystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.personneltrackingsystem.entity.Personel;
import com.personneltrackingsystem.dto.DtoDailyPersonnelEntry;

import java.util.HashMap;
import java.util.List;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Personel> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Personel> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Configure serializers
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringRedisSerializer);
        template.setHashKeySerializer(stringRedisSerializer);

        // Create ObjectMapper with JSR310 module
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        
        // Configure JSON serialization for values using constructor approach
        Jackson2JsonRedisSerializer<Personel> jsonRedisSerializer = 
            new Jackson2JsonRedisSerializer<>(objectMapper, Personel.class);
        
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
        
        // Configure JSON serialization for HashMap<String, List<DtoDailyPersonnelEntry>>
        Jackson2JsonRedisSerializer<HashMap> jsonRedisSerializer = 
            new Jackson2JsonRedisSerializer<>(objectMapper, HashMap.class);
        
        template.setValueSerializer(jsonRedisSerializer);
        template.setHashValueSerializer(jsonRedisSerializer);

        template.afterPropertiesSet();
        return template;
    }
} 