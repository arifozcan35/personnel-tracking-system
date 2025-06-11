package com.personneltrackingsystem.dto.event;

import com.personneltrackingsystem.entity.OperationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TurnstileRequestEvent implements Serializable {

    private Long wantedToEnterTurnstileId;

    private Long personelId;

    private OperationType operationType;
    
    private String operationTimeStr;
} 