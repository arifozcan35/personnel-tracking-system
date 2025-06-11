package com.personneltrackingsystem.service.kafka;

import com.personneltrackingsystem.dto.event.EmailEvent;
import com.personneltrackingsystem.dto.event.TurnstilePassageEvent;
import com.personneltrackingsystem.dto.event.TurnstileRequestEvent;
 
public interface KafkaProducerService {

    void sendTurnstilePassageEvent(TurnstilePassageEvent event);
    
    void sendEmailEvent(EmailEvent event);
    
    void sendTurnstileRequestEvent(TurnstileRequestEvent event);
} 