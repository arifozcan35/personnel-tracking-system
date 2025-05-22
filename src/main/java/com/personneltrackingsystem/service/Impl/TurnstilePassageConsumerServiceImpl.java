package com.personneltrackingsystem.service.Impl;

import com.personneltrackingsystem.config.KafkaConfig;
import com.personneltrackingsystem.event.EmailEvent;
import com.personneltrackingsystem.event.TurnstilePassageEvent;
import com.personneltrackingsystem.service.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class TurnstilePassageConsumerServiceImpl {

    private final KafkaProducerService kafkaProducerService;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @KafkaListener(topics = KafkaConfig.TURNSTILE_PASSAGE_TOPIC, groupId = "${spring.kafka.consumer.group-id}")
    public void consumeTurnstilePassageEvent(TurnstilePassageEvent event) {
        log.info("Received turnstile passage event: {}", event);
        
        // Create email event based on turnstile passage
        EmailEvent emailEvent = createEmailEvent(event);
        
        // Send email event to Kafka
        kafkaProducerService.sendEmailEvent(emailEvent);
    }
    
    private EmailEvent createEmailEvent(TurnstilePassageEvent event) {
        String action = event.getOperationType().equals("IN") ? "entered" : "exited";
        String subject = "Turnstile Passage Notification - " + event.getOperationType();
        
        String message = String.format(
            "Dear %s,\n\n" +
            "This is to inform you that you have %s the facility through turnstile \"%s\" at %s.\n\n" +
            "Details:\n" +
            "- Personnel Name: %s\n" +
            "- Turnstile Name: %s\n" +
            "- Date/Time: %s\n" +
            "- Operation: %s\n\n\n" +
            "From the Personnel Tracking System\n",
            event.getPersonelName(),
            action,
            event.getTurnstileName(),
            event.getPassageTime().format(formatter),
            event.getPersonelName(),
            event.getTurnstileName(),
            event.getPassageTime().format(formatter),
            event.getOperationType()
        );
        
        return new EmailEvent(
            event.getPersonelEmail(),
            event.getPersonelName(),
            subject,
            message,
            LocalDateTime.now()
        );
    }
} 