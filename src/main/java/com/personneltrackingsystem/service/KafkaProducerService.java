package com.personneltrackingsystem.service;

import com.personneltrackingsystem.dto.EmailNotificationEventDto;
import com.personneltrackingsystem.dto.GatePassageEventDto;
import com.personneltrackingsystem.dto.WorkValidationEventDto;

public interface KafkaProducerService {

    void sendGatePassageEvent(GatePassageEventDto gatePassageEvent);

    void sendWorkValidationEvent(WorkValidationEventDto workValidationEvent);
    
    void sendEmailNotificationEvent(EmailNotificationEventDto emailNotificationEvent);
} 