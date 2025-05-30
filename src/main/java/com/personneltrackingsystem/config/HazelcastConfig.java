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
        
        // Configure the monthly personnel list cache
        MapConfig monthlyPersonnelMapConfig = new MapConfig();
        monthlyPersonnelMapConfig.setName("monthlyPersonnelList");
        monthlyPersonnelMapConfig.setTimeToLiveSeconds(604800); // 7 days
        monthlyPersonnelMapConfig.setMaxIdleSeconds(86400); // 24 hours idle time
        config.addMapConfig(monthlyPersonnelMapConfig);
        
        return Hazelcast.newHazelcastInstance(config);
    }
} 