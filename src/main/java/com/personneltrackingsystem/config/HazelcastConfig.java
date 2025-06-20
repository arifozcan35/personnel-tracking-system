package com.personneltrackingsystem.config;

import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HazelcastConfig {

    @Value("${hazelcast.instance.name:personnel-tracking-system}")
    private String instanceName;
    
    @Value("${hazelcast.network.port:5701}")
    private int port;
    
    @Value("${hazelcast.network.port-auto-increment:true}")
    private boolean portAutoIncrement;
    
    @Value("${hazelcast.cluster.name:pts-cluster}")
    private String clusterName;

    @Bean
    public HazelcastInstance hazelcastInstance() {
        Config config = new Config();
        config.setInstanceName(instanceName);
        config.setClusterName(clusterName);

        NetworkConfig networkConfig = config.getNetworkConfig();
        networkConfig.setPort(port);
        networkConfig.setPortAutoIncrement(portAutoIncrement);
        
        JoinConfig joinConfig = networkConfig.getJoin();
        joinConfig.getMulticastConfig().setEnabled(false);
        joinConfig.getTcpIpConfig().setEnabled(true);
        joinConfig.getTcpIpConfig().addMember("127.0.0.1");

        MapConfig monthlyPersonnelMapConfig = new MapConfig();
        monthlyPersonnelMapConfig.setName("monthlyPersonnelList");
        monthlyPersonnelMapConfig.setTimeToLiveSeconds(604800); // 7 days
        monthlyPersonnelMapConfig.setMaxIdleSeconds(86400); // 24 hours idle time
        config.addMapConfig(monthlyPersonnelMapConfig);
        
        return Hazelcast.newHazelcastInstance(config);
    }
} 