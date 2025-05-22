package com.personneltrackingsystem.service.Impl;

import com.personneltrackingsystem.config.KafkaConfig;
import com.personneltrackingsystem.event.EmailEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailConsumerServiceImpl {

    private final JavaMailSender mailSender;

    @KafkaListener(topics = KafkaConfig.EMAIL_NOTIFICATION_TOPIC, groupId = "${spring.kafka.consumer.group-id}")
    public void consumeEmailEvent(EmailEvent event) {
        log.info("Received email event: {}", event);
        sendEmail(event);
    }
    
    private void sendEmail(EmailEvent event) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(event.getRecipientEmail());
            message.setSubject(event.getSubject());
            message.setText(event.getMessage());
            
            mailSender.send(message);
            log.info("Email sent successfully to {}", event.getRecipientEmail());
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", event.getRecipientEmail(), e.getMessage());
        }
    }
} 