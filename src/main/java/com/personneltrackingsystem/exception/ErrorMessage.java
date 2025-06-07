package com.personneltrackingsystem.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
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
        String resolvedMessage = messageResolver.getMessage(messageType.getMessageKey());
        builder.append(resolvedMessage);
        if(ofStatic!=null) {
            builder.append(" : " + ofStatic);
        }
        return builder.toString();
    }
}
