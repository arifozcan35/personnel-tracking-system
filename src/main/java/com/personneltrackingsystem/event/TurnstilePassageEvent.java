package com.personneltrackingsystem.event;

import lombok.AllArgsConstructor;
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
    
    private OperationType operationType;  // IN or OUT
    
    // For admin notifications
    private String recipientEmail; // The email address to send the notification to
    private String recipientName;  // The name of the recipient (admin)
    private Boolean isAdminNotification = false;
    
    // For late arrival notifications
    private Boolean isLateArrival = false;
    private Long minutesLate = 0L;

    // Constructor with original fields
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
        
        // By default, the recipient is the person who passed through
        this.recipientEmail = personelEmail;
        this.recipientName = personelName;
    }
} 