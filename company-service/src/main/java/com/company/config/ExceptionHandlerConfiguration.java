package com.company.config;

import com.company.exception.AddressNotFoundException;
import com.company.exception.CompanyMemberAdditionException;
import com.company.exception.CompanyNonExistException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionHandlerConfiguration {

    @ExceptionHandler(value = {CompanyNonExistException.class, AddressNotFoundException.class})
    public ResponseEntity<?> handleNonExistOrNotFoundException(CompanyNonExistException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CompanyMemberAdditionException.class)
    public ResponseEntity<?> handleCompanyMemberAdditionException(CompanyMemberAdditionException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGenericException(Exception exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
