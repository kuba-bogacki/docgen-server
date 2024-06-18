package com.authentication.exception;

public class UserAccountDisableException extends RuntimeException {

    public UserAccountDisableException(String message) {
        super(message);
    }

    public UserAccountDisableException(String message, Throwable cause) {
        super(message, cause);
    }
}
