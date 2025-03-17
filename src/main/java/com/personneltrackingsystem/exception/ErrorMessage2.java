package com.personneltrackingsystem.exception;

public class ErrorMessage2 {
    private MessageType2 messageType;
    private String ofStatic;
    private MessageResolver messageResolver;

    public ErrorMessage2(MessageType2 messageType, MessageResolver messageResolver) {
        this.messageType = messageType;
        this.messageResolver = messageResolver;
    }

    public ErrorMessage2(MessageType2 messageType, String ofStatic, MessageResolver messageResolver) {
        this.messageType = messageType;
        this.ofStatic = ofStatic;
        this.messageResolver = messageResolver;
    }

    public String prepareErrorMessage() {
        StringBuilder builder = new StringBuilder();
        String localizedMessage = messageResolver.getMessage(messageType.getMessageKey());
        builder.append(localizedMessage);

        if(ofStatic != null) {
            builder.append(" : " + ofStatic);
        }

        return builder.toString();
    }

    public MessageType2 getMessageType() {
        return messageType;
    }
}
