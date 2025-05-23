package com.personneltrackingsystem.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.personneltrackingsystem.entity.OperationType;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TurnstilePassageEvent implements Serializable {

    private Long personelId;

    private String personelName;

    private String personelEmail;

    private Long turnstileId;

    private String turnstileName;

    private LocalDateTime passageTime;
    
    private OperationType operationType;  // IN or OUT
} 