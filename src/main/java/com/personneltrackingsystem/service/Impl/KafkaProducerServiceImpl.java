package com.personneltrackingsystem.service.Impl;

import com.personneltrackingsystem.dto.EmailNotificationEventDto;
import com.personneltrackingsystem.dto.GatePassageEventDto;
import com.personneltrackingsystem.dto.WorkValidationEventDto;
import com.personneltrackingsystem.service.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerServiceImpl implements KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${app.kafka.topic.gate-passage}")
    private String gatePassageTopic;

    @Value("${app.kafka.topic.work-validation}")
    private String workValidationTopic;

    @Value("${app.kafka.topic.email-notification}")
    private String emailNotificationTopic;

    @Override
    public void sendGatePassageEvent(GatePassageEventDto gatePassageEvent) {
        log.info("Sending gate passage event to Kafka: {}", gatePassageEvent);
        kafkaTemplate.send(gatePassageTopic, gatePassageEvent);
    }

    @Override
    public void sendWorkValidationEvent(WorkValidationEventDto workValidationEvent) {
        log.info("Sending work validation event to Kafka: {}", workValidationEvent);
        kafkaTemplate.send(workValidationTopic, workValidationEvent);
    }

    @Override
    public void sendEmailNotificationEvent(EmailNotificationEventDto emailNotificationEvent) {
        log.info("Sending email notification event to Kafka: {}", emailNotificationEvent);
        kafkaTemplate.send(emailNotificationTopic, emailNotificationEvent);
    }
} 