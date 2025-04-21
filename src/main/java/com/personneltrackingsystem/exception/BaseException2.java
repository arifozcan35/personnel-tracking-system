package com.personneltrackingsystem.exception;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BaseException2 extends RuntimeException {

    private final ErrorMessage2 errorMessage;

    public ErrorMessage2 getErrorMessage() {
        return errorMessage;
    }

}
