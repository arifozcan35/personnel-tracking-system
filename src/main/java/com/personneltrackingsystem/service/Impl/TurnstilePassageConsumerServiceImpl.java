package com.personneltrackingsystem.service.impl;

import com.personneltrackingsystem.config.KafkaConfig;
import com.personneltrackingsystem.event.EmailEvent;
import com.personneltrackingsystem.event.TurnstilePassageEvent;
import com.personneltrackingsystem.exception.BaseException;
import com.personneltrackingsystem.exception.MessageType;
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
        
        EmailEvent emailEvent = createEmailEvent(event);
        
        kafkaProducerService.sendEmailEvent(emailEvent);
    }
    
    private EmailEvent createEmailEvent(TurnstilePassageEvent event) {
        // for admin notifications about late arrivals
        if (Boolean.TRUE.equals(event.getIsAdminNotification()) && Boolean.TRUE.equals(event.getIsLateArrival())) {
            return createLateArrivalEmailEvent(event);
        }else{
            throw new BaseException(MessageType.INVALID_EVENT_TYPE);
        }
    }
    
    private EmailEvent createLateArrivalEmailEvent(TurnstilePassageEvent event) {
        String subject = "Late Arrival Notification - " + event.getPersonelName();
        
        String message = String.format(
            "Dear %s,\n\n" +
            "This is to inform you that %s has arrived late to work today.\n\n" +
            "Details:\n" +
            "- Personnel Name: %s\n" +
            "- Arrival Time: %s\n" +
            "- Minutes Late: %d minutes\n" +
            "- Entrance: %s\n\n\n" +
            "This is an automated message from the Personnel Tracking System.\n",
            event.getRecipientName(), // Admin's name
            event.getPersonelName(),  // Late personnel
            event.getPersonelName(),
            event.getPassageTime().format(formatter),
            event.getMinutesLate(),
            event.getTurnstileName()
        );
        
        return new EmailEvent(
            event.getRecipientEmail(), // Admin's email
            event.getRecipientName(),  // Admin's name
            subject,
            message,
            LocalDateTime.now()
        );
    }
} 