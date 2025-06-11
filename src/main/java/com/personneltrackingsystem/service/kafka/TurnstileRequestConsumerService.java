package com.personneltrackingsystem.service.kafka;

import com.personneltrackingsystem.dto.event.TurnstileRequestEvent;
import org.springframework.kafka.support.Acknowledgment;

public interface TurnstileRequestConsumerService {

    void consumeTurnstileRequestEvent(TurnstileRequestEvent event, Acknowledgment acknowledgment);

}
