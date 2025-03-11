package com.personneltrackingsystem.exception;

import lombok.Getter;

@Getter
public enum MessageType {

    NO_RECORD_EXIST("1001","No record found!"),
    REQUIRED_FIELD_AVAILABLE("5000", "Name information cannot be left blank!"),
    GENERAL_EXCEPTION("9999" , "A general error has occurred!");

    private String code;
    private String message;



    MessageType(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
