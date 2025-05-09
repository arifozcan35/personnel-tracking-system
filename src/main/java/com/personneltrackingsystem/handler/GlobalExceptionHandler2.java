package com.personneltrackingsystem.handler;

import com.personneltrackingsystem.exception.BaseException2;
import com.personneltrackingsystem.exception.MessageResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

@ControllerAdvice
public class GlobalExceptionHandler2 {

    @Autowired
    private MessageResolver messageResolver;

    @ExceptionHandler(value = { BaseException2.class })
    public ResponseEntity<ApiError2> handleBaseException(BaseException2 exception, WebRequest request) {
        String code = null;
        if (exception.getErrorMessage() != null && exception.getErrorMessage().getMessageType() != null) {
            code = exception.getErrorMessage().getMessageType().getCode();
        }
        return ResponseEntity.badRequest().body(createApiError(exception.getMessage(), request, code));
    }

    public <E> ApiError2<E> createApiError(E message, WebRequest request, String code) {
        ApiError2<E> apiError = new ApiError2<>();
        apiError.setStatus(HttpStatus.BAD_REQUEST.value());
        apiError.setCode(code);

        CustomException<E> customException = new CustomException<>();
        customException.setCreateTime(new Date());
        customException.setHostName(getHostname());
        customException.setPath(request.getDescription(false).substring(4));
        customException.setMessage(message);
        apiError.setException(customException);

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
