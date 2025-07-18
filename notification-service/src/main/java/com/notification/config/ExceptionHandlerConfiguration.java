package com.notification.config;

import com.notification.exception.ReadEmailContentException;
import com.notification.exception.SendEmailException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionHandlerConfiguration {

    @ExceptionHandler(SendEmailException.class)
    public ResponseEntity<?> handleSendEmailException(SendEmailException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(ReadEmailContentException.class)
    public ResponseEntity<?> handleReadEmailContentException(ReadEmailContentException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGenericException(Exception exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
