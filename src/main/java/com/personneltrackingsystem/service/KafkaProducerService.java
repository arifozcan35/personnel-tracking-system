package com.personneltrackingsystem.service;

import com.personneltrackingsystem.event.EmailEvent;
import com.personneltrackingsystem.event.TurnstilePassageEvent;
import com.personneltrackingsystem.event.TurnstileRequestEvent;
 
public interface KafkaProducerService {

    void sendTurnstilePassageEvent(TurnstilePassageEvent event);
    
    void sendEmailEvent(EmailEvent event);
    
    void sendTurnstileRequestEvent(TurnstileRequestEvent event);
} 