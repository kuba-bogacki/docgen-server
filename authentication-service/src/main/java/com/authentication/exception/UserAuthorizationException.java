package com.authentication.exception;

public class UserAuthorizationException extends RuntimeException {

    public UserAuthorizationException(String message) {
        super(message);
    }

    public UserAuthorizationException(String message, Throwable cause) {
        super(message, cause);
    }
}
