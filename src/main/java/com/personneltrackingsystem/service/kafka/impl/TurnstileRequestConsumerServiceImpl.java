package com.personneltrackingsystem.service.kafka.impl;

import com.personneltrackingsystem.config.KafkaConfig;
import com.personneltrackingsystem.dto.DtoTurnstilePassageFullRequest;
import com.personneltrackingsystem.dto.event.TurnstileRequestEvent;
import com.personneltrackingsystem.service.kafka.TurnstileRequestConsumerService;
import com.personneltrackingsystem.service.TurnstileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TurnstileRequestConsumerServiceImpl implements TurnstileRequestConsumerService {

    private final TurnstileService turnstileService;

    @Override
    @KafkaListener(topics = KafkaConfig.TURNSTILE_REQUEST_TOPIC, groupId = "${spring.kafka.consumer.group-id}-reset")
    public void consumeTurnstileRequestEvent(TurnstileRequestEvent event, Acknowledgment acknowledgment) {
        log.info("Received turnstile request event: {}", event);
        
        try {
            DtoTurnstilePassageFullRequest request = new DtoTurnstilePassageFullRequest(
                event.getWantedToEnterTurnstileId(),
                event.getPersonelId(),
                event.getOperationType(),
                event.getOperationTimeStr()
            );

            turnstileService.passTurnstile(request);

            acknowledgment.acknowledge();
            log.info("Turnstile request event processed successfully");
            
        } catch (Exception e) {
            log.error("Error processing turnstile request event: {}", e.getMessage(), e);
            acknowledgment.acknowledge();
        }
    }
} 