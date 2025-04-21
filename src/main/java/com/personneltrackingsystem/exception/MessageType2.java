package com.personneltrackingsystem.exception;

public enum MessageType2 {
    NO_RECORD_EXIST("1001","No record found!"),
    REQUIRED_FIELD_AVAILABLE("5000", "Name information cannot be left blank!"),
    GENERAL_EXCEPTION("9999" , "A general error has occurred!");

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
