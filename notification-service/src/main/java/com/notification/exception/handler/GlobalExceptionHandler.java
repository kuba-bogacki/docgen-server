package com.notification.exception.handler;

import com.notification.exception.CurrentUserNotFoundException;
import com.notification.exception.ReadEmailContentException;
import com.notification.exception.SendEmailException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(SendEmailException.class)
    public ResponseEntity<String> handleSendEmailException(SendEmailException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(ReadEmailContentException.class)
    public ResponseEntity<String> handleReadEmailContentException(ReadEmailContentException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(CurrentUserNotFoundException.class)
    public ResponseEntity<String> handleCurrentUserNotFoundException(CurrentUserNotFoundException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
