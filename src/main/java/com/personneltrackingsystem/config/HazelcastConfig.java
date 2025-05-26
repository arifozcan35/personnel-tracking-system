package com.personneltrackingsystem.config;

import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HazelcastConfig {

    @Bean
    public HazelcastInstance hazelcastInstance() {
        Config config = new Config();
        config.setInstanceName("personnel-tracking-system");
        
        // Configure the daily personnel list cache
        MapConfig dailyPersonnelMapConfig = new MapConfig();
        dailyPersonnelMapConfig.setName("dailyPersonnelList");
        dailyPersonnelMapConfig.setTimeToLiveSeconds(86400); // 24 hours
        dailyPersonnelMapConfig.setMaxIdleSeconds(3600); // 1 hour idle time
        config.addMapConfig(dailyPersonnelMapConfig);
        
        return Hazelcast.newHazelcastInstance(config);
    }
} 