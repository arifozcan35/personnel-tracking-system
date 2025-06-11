package com.personneltrackingsystem.service.kafka;

import com.personneltrackingsystem.dto.event.EmailEvent;
import org.springframework.kafka.support.Acknowledgment;

public interface EmailConsumerService {

    void consumeEmailEvent(EmailEvent event, Acknowledgment acknowledgment);

    void sendEmail(EmailEvent event);
}
