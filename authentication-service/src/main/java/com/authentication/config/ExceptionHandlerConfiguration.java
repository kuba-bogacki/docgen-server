package com.authentication.config;

import com.authentication.exception.UserAuthorizationException;
import com.authentication.exception.UserPaymentSessionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionHandlerConfiguration {

    @ExceptionHandler(UserAuthorizationException.class)
    public ResponseEntity<?> handleUserAuthorizationException(UserAuthorizationException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(UserPaymentSessionException.class)
    public ResponseEntity<?> handleUserPaymentSessionException(UserPaymentSessionException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGenericException(Exception exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
