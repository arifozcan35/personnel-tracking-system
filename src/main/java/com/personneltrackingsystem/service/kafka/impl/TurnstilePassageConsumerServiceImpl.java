package com.personneltrackingsystem.service.kafka.impl;

import com.personneltrackingsystem.config.KafkaConfig;
import com.personneltrackingsystem.dto.event.EmailEvent;
import com.personneltrackingsystem.dto.event.TurnstilePassageEvent;
import com.personneltrackingsystem.exception.BaseException;
import com.personneltrackingsystem.exception.MessageType;
import com.personneltrackingsystem.service.kafka.KafkaProducerService;
import com.personneltrackingsystem.service.kafka.TurnstilePassageConsumerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class TurnstilePassageConsumerServiceImpl implements TurnstilePassageConsumerService {

    private final KafkaProducerService kafkaProducerService;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    @Override
    @KafkaListener(topics = KafkaConfig.TURNSTILE_PASSAGE_TOPIC, groupId = "${spring.kafka.consumer.group-id}-reset")
    public void consumeTurnstilePassageEvent(TurnstilePassageEvent event, Acknowledgment acknowledgment) {
        log.info("Received turnstile passage event: {}", event);
        
        try {
            EmailEvent emailEvent = createEmailEvent(event);
            kafkaProducerService.sendEmailEvent(emailEvent);
            
            // after the message is processed, acknowledge
            acknowledgment.acknowledge();
            log.info("Turnstile passage event processed successfully");
        } catch (Exception e) {
            log.error("Error processing turnstile passage event: {}", e.getMessage(), e);
            // acknowledge manually (to prevent message from being sent to DLQ)
            // this way the same message is not processed again
            acknowledgment.acknowledge();
        }
    }

    @Override
    public EmailEvent createEmailEvent(TurnstilePassageEvent event) {
        // for admin notifications about late arrivals
        if (Boolean.TRUE.equals(event.getIsAdminNotification()) && Boolean.TRUE.equals(event.getIsLateArrival())) {
            return createLateArrivalEmailEvent(event);
        }else{
            throw new BaseException(MessageType.INVALID_EVENT_TYPE);
        }
    }

    @Override
    public EmailEvent createLateArrivalEmailEvent(TurnstilePassageEvent event) {
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
            event.getRecipientName(),
            event.getPersonelName(),
            event.getPersonelName(),
            event.getPassageTime().format(formatter),
            event.getMinutesLate(),
            event.getTurnstileName()
        );
        
        return new EmailEvent(
            event.getRecipientEmail(),
            event.getRecipientName(), 
            subject,
            message,
            LocalDateTime.now()
        );
    }
} 