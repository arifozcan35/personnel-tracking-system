package com.personneltrackingsystem.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {
    
    public static final String TURNSTILE_PASSAGE_TOPIC = "turnstile-passage";
    public static final String EMAIL_NOTIFICATION_TOPIC = "email-notification";
    
    @Bean
    public NewTopic turnstilePassageTopic() {
        return TopicBuilder.name(TURNSTILE_PASSAGE_TOPIC)
                .partitions(1)
                .replicas(1)
                .build();
    }
    
    @Bean
    public NewTopic emailNotificationTopic() {
        return TopicBuilder.name(EMAIL_NOTIFICATION_TOPIC)
                .partitions(1)
                .replicas(1)
                .build();
    }
} 