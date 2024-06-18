package com.authentication.exception;

public class UserPasswordException extends RuntimeException {

    public UserPasswordException(String message) {
        super(message);
    }

    public UserPasswordException(String message, Throwable cause) {
        super(message, cause);
    }
}
