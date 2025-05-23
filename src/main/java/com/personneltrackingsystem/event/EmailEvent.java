package com.personneltrackingsystem.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailEvent implements Serializable {

    private String recipientEmail;

    private String recipientName;

    private String subject;

    private String message;
    
    private LocalDateTime timestamp;
} 