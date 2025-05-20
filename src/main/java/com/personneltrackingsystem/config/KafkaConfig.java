/* 
package com.personneltrackingsystem.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Value("${app.kafka.topic.gate-passage}")
    private String gatePassageTopic;

    @Value("${app.kafka.topic.work-validation}")
    private String workValidationTopic;

    @Value("${app.kafka.topic.email-notification}")
    private String emailNotificationTopic;

    @Bean
    public NewTopic gatePassageTopic() {
        return TopicBuilder.name(gatePassageTopic)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic workValidationTopic() {
        return TopicBuilder.name(workValidationTopic)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic emailNotificationTopic() {
        return TopicBuilder.name(emailNotificationTopic)
                .partitions(1)
                .replicas(1)
                .build();
    }
} 
    */