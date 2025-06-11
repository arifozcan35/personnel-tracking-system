package com.personneltrackingsystem.dto.event;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.personneltrackingsystem.entity.OperationType;

@Data
@NoArgsConstructor
public class TurnstilePassageEvent implements Serializable {

    private Long personelId;

    private String personelName;

    private String personelEmail;

    private Long turnstileId;

    private String turnstileName;

    private LocalDateTime passageTime;
    
    private OperationType operationType;

    private String recipientEmail;
    private String recipientName;
    private Boolean isAdminNotification = false;

    private Boolean isLateArrival = false;
    private Long minutesLate = 0L;

    public TurnstilePassageEvent(Long personelId, String personelName, String personelEmail, 
                                Long turnstileId, String turnstileName, LocalDateTime passageTime, 
                                OperationType operationType) {
        this.personelId = personelId;
        this.personelName = personelName;
        this.personelEmail = personelEmail;
        this.turnstileId = turnstileId;
        this.turnstileName = turnstileName;
        this.passageTime = passageTime;
        this.operationType = operationType;

        this.recipientEmail = personelEmail;
        this.recipientName = personelName;
    }
} 