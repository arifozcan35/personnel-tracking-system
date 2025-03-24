package com.personneltrackingsystem.exception;

public enum MessageType2 {
    NO_RECORD_EXIST("1001","no.record.found"),
    REQUIRED_FIELD_AVAILABLE("5000", "name.required"),
    GENERAL_EXCEPTION("9999", "general.exception");

    private String code;
    private String messageKey;

    MessageType2(String code, String messageKey) {
        this.code = code;
        this.messageKey = messageKey;
    }

    public String getCode() {
        return code;
    }

    public String getMessageKey() {
        return messageKey;
    }
}
