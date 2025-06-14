package com.personneltrackingsystem.exception;

public class BaseException extends RuntimeException{
    
    private final MessageType messageType;
    
    public BaseException() {
        this.messageType = null;
    }

    public BaseException(ErrorMessage errorMessage) {
        super(errorMessage.prepareErrorMessage());
        this.messageType = errorMessage.getMessageType();
    }
    
    public BaseException(MessageType messageType) {
        super(messageType.getMessageKey());
        this.messageType = messageType;
    }
    
    public BaseException(MessageType messageType, String additionalInfo) {
        super(messageType.getMessageKey() + (additionalInfo != null ? " : " + additionalInfo : ""));
        this.messageType = messageType;
    }
    
    public MessageType getMessageType() {
        return messageType;
    }
}
