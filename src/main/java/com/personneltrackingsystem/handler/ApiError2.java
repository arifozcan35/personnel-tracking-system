package com.personneltrackingsystem.handler;

public class ApiError2<E> {
    private Integer status;
    private String code;
    private CustomException<E> exception;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public CustomException<E> getException() {
        return exception;
    }

    public void setException(CustomException<E> exception) {
        this.exception = exception;
    }
}
