package com.notification.exception;

public class EventSendFailureException extends RuntimeException {

    public EventSendFailureException(String message) {
        super(message);
    }
}
