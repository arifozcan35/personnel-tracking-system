package com.personneltrackingsystem.exception;

public class ValidationException extends RuntimeException {

    private final String message;

    public ValidationException(String message) {
        super(message);
        this.message = message;
    }

    public ValidationException(MessageType messageType) {
        super(messageType.getMessage());
        this.message = messageType.getMessage();
    }

    public ValidationException(MessageType messageType, String additionalInfo) {
        super(messageType.getMessage() + (additionalInfo != null ? " : " + additionalInfo : ""));
        this.message = messageType.getMessage() + (additionalInfo != null ? " : " + additionalInfo : "");
    }

    @Override
    public String getMessage() {
        return message;
    }
}
