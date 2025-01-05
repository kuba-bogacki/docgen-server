package com.notification.exception;

public class CurrentUserNotFoundException extends RuntimeException {

    public CurrentUserNotFoundException(String message) {
        super(message);
    }
}
