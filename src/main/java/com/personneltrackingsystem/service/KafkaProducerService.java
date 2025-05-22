package com.personneltrackingsystem.service;

import com.personneltrackingsystem.event.EmailEvent;
import com.personneltrackingsystem.event.TurnstilePassageEvent;
 
public interface KafkaProducerService {
    void sendTurnstilePassageEvent(TurnstilePassageEvent event);
    void sendEmailEvent(EmailEvent event);
} 