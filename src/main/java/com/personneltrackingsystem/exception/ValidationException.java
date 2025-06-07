package com.personneltrackingsystem.exception;

public class ValidationException extends RuntimeException {

    private final String message;

    public ValidationException(String message) {
        super(message);
        this.message = message;
    }

    public ValidationException(MessageType messageType) {
        super(messageType.getMessageKey());
        this.message = messageType.getMessageKey();
    }

    public ValidationException(MessageType messageType, String additionalInfo) {
        super(messageType.getMessageKey() + (additionalInfo != null ? " : " + additionalInfo : ""));
        this.message = messageType.getMessageKey() + (additionalInfo != null ? " : " + additionalInfo : "");
    }

    @Override
    public String getMessage() {
        return message;
    }
}
