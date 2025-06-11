package com.personneltrackingsystem.service.kafka;

import com.personneltrackingsystem.dto.event.EmailEvent;
import com.personneltrackingsystem.dto.event.TurnstilePassageEvent;
import org.springframework.kafka.support.Acknowledgment;

public interface TurnstilePassageConsumerService {

    void consumeTurnstilePassageEvent(TurnstilePassageEvent event, Acknowledgment acknowledgment);

    EmailEvent createEmailEvent(TurnstilePassageEvent event);

    EmailEvent createLateArrivalEmailEvent(TurnstilePassageEvent event);
}
