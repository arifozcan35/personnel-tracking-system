package com.personneltrackingsystem.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {
    
    public static final String TURNSTILE_PASSAGE_TOPIC = "turnstile-passage";

    public static final String EMAIL_NOTIFICATION_TOPIC = "email-notification";

    public static final String TURNSTILE_REQUEST_TOPIC = "turnstile-request";

    public static final String TURNSTILE_DLQ_TOPIC = "turnstile-request-dlt";
    
    
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;
    
    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;
    
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
    
    @Bean
    public NewTopic turnstileRequestTopic() {
        return TopicBuilder.name(TURNSTILE_REQUEST_TOPIC)
                .partitions(1)
                .replicas(1)
                .build();
    }
    
    @Bean
    public NewTopic turnstileDltTopic() {
        return TopicBuilder.name(TURNSTILE_DLQ_TOPIC)
                .partitions(1)
                .replicas(1)
                .build();
    }
    
    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        configProps.put(ProducerConfig.ACKS_CONFIG, "all");
        configProps.put(ProducerConfig.RETRIES_CONFIG, 3);
        configProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        return new DefaultKafkaProducerFactory<>(configProps);
    }
    
    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
    
    @Bean
    public ConsumerFactory<String, Object> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId + "-reset");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.personneltrackingsystem.*");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        
        return new DefaultKafkaConsumerFactory<>(props);
    }
    
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory(
            KafkaTemplate<String, Object> kafkaTemplate) {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory = 
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        
        // DLQ
        DeadLetterPublishingRecoverer recoverer = 
                new DeadLetterPublishingRecoverer(kafkaTemplate,
                        (record, exception) -> new TopicPartition(record.topic() + "-dlt", record.partition()));
        
        // Retry 3 times
        DefaultErrorHandler errorHandler = new DefaultErrorHandler(
                recoverer, new FixedBackOff(1000, 3));
        
        factory.setCommonErrorHandler(errorHandler);
        return factory;
    }
} 