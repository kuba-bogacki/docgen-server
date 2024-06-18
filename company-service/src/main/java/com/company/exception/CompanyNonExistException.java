package com.company.exception;

public class CompanyNonExistException extends RuntimeException {

    public CompanyNonExistException(String message) {
        super(message);
    }

    public CompanyNonExistException(String message, Throwable cause) {
        super(message, cause);
    }
}
