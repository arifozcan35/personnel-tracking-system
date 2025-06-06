package com.personneltrackingsystem.service.impl;

import com.personneltrackingsystem.config.KafkaConfig;
import com.personneltrackingsystem.event.EmailEvent;
import com.personneltrackingsystem.event.TurnstilePassageEvent;
import com.personneltrackingsystem.event.TurnstileRequestEvent;
import com.personneltrackingsystem.service.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerServiceImpl implements KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    
    @Override
    public void sendTurnstilePassageEvent(TurnstilePassageEvent event) {
        log.info("Sending turnstile passage event to Kafka: {}", event);
        try {
            CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(KafkaConfig.TURNSTILE_PASSAGE_TOPIC, event);
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Turnstile passage event sent successfully to topic: {}, partition: {}, offset: {}",
                            result.getRecordMetadata().topic(),
                            result.getRecordMetadata().partition(),
                            result.getRecordMetadata().offset());
                } else {
                    log.error("Failed to send turnstile passage event: {}", ex.getMessage(), ex);
                }
            });
        } catch (Exception e) {
            log.error("Error sending turnstile passage event: {}", e.getMessage(), e);
        }
    }


    @Override
    public void sendEmailEvent(EmailEvent event) {
        log.info("Sending email event to Kafka: {}", event);
        try {
            CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(KafkaConfig.EMAIL_NOTIFICATION_TOPIC, event);
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Email event sent successfully to topic: {}, partition: {}, offset: {}",
                            result.getRecordMetadata().topic(),
                            result.getRecordMetadata().partition(),
                            result.getRecordMetadata().offset());
                } else {
                    log.error("Failed to send email event: {}", ex.getMessage(), ex);
                }
            });
        } catch (Exception e) {
            log.error("Error sending email event: {}", e.getMessage(), e);
        }
    }
    
    @Override
    public void sendTurnstileRequestEvent(TurnstileRequestEvent event) {
        log.info("Sending turnstile request event to Kafka: {}", event);
        try {
            CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(KafkaConfig.TURNSTILE_REQUEST_TOPIC, event);
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Turnstile request event sent successfully to topic: {}, partition: {}, offset: {}",
                            result.getRecordMetadata().topic(),
                            result.getRecordMetadata().partition(),
                            result.getRecordMetadata().offset());
                } else {
                    log.error("Failed to send turnstile request event: {}", ex.getMessage(), ex);
                }
            });
        } catch (Exception e) {
            log.error("Error sending turnstile request event: {}", e.getMessage(), e);
        }
    }
} 