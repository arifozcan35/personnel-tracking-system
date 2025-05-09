package com.personneltrackingsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailNotificationEventDto {
    private Long personelId;
    private String personelName;
    private String personelEmail;
    private String subject;
    private String message;
    private Boolean isWorkValid;
    private LocalDateTime notificationTime;
} 