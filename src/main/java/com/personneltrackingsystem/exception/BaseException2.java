package com.personneltrackingsystem.exception;

public class BaseException2 extends RuntimeException {

    private final ErrorMessage2 errorMessage;

    public BaseException2() {
        this.errorMessage = null;
    }

    public BaseException2(ErrorMessage2 errorMessage) {
        super(errorMessage.prepareErrorMessage());
        this.errorMessage = errorMessage;
    }

    public ErrorMessage2 getErrorMessage() {
        return errorMessage;
    }
}
