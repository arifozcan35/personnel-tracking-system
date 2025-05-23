package com.personneltrackingsystem.handler;

import com.personneltrackingsystem.exception.BaseException;
import com.personneltrackingsystem.exception.MessageType;
import com.personneltrackingsystem.exception.ValidationException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = { BaseException.class })
    public ResponseEntity<ApiError> handleBaseException(BaseException exception, WebRequest request) {
        HttpStatus status = determineStatusFromMessageType(exception.getMessageType());
        return ResponseEntity.status(status).body(createApiError(exception.getMessage(), request, status));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiError> handleEntityNotFoundException(EntityNotFoundException exception, WebRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(createApiError(exception.getMessage(), request, HttpStatus.NOT_FOUND));
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiError> handleValidationException(ValidationException exception, WebRequest request) {
        return ResponseEntity.badRequest().body(createApiError(exception.getMessage(), request, HttpStatus.BAD_REQUEST));
    }

    private HttpStatus determineStatusFromMessageType(MessageType messageType) {
        if (messageType == null) {
            return HttpStatus.BAD_REQUEST;
        }
        
        // Entity existence errors should return 404 NOT FOUND
        switch (messageType) {
            case NO_RECORD_EXIST:
            case BUILDING_NOT_FOUND:
            case FLOOR_NOT_FOUND:
            case UNIT_NOT_FOUND:
            case GATE_NOT_FOUND:
            case TURNSTILE_NOT_FOUND:
            case PERSONNEL_NOT_FOUND:
            case PERSONNEL_TYPE_NOT_FOUND:
            case WORKING_HOURS_NOT_FOUND:
            case PERMISSION_NOT_FOUND:
            case ROLE_NOT_FOUND:
                return HttpStatus.NOT_FOUND;
                
            // All other errors are bad request
            default:
                return HttpStatus.BAD_REQUEST;
        }
    }

    public <E> ApiError<E> createApiError(E message, WebRequest request, HttpStatus status){
        ApiError<E> apiError = new ApiError<>();
        apiError.setStatus(status.value());

        ExceptionDetail<E> exceptionDetail = new ExceptionDetail<>();

        exceptionDetail.setCreateTime(new Date());
        exceptionDetail.setHostName(getHostname());
        exceptionDetail.setPath(request.getDescription(false).substring(4));
        exceptionDetail.setMessage(message);

        apiError.setException(exceptionDetail);

        return apiError;
    }

    private String getHostname() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            System.out.println("hata oluştu " + e.getMessage());
        }

        return null;
    }
}
