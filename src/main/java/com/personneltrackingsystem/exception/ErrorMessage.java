package com.personneltrackingsystem.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Component
public class ErrorMessage {

    private MessageResolver messageResolver;

    private MessageType messageType;

    private String ofStatic;

    public ErrorMessage(MessageType messageType, String ofStatic) {
        this.messageType = messageType;
        this.ofStatic = ofStatic;
    }

    public String prepareErrorMessage() {
        StringBuilder builder = new StringBuilder();
        
        // If MessageResolver is null, use messageType's messageKey directly
        if (ObjectUtils.isNotEmpty(messageResolver)) {
            String resolvedMessage = messageResolver.getMessage(messageType.getMessageKey());
            builder.append(resolvedMessage);
        } else {
            builder.append(messageType.getMessageKey());
        }
        
        if(ObjectUtils.isNotEmpty(ofStatic)) {
            builder.append(" : " + ofStatic);
        }
        return builder.toString();
    }
}
