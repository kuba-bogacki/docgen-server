package com.document.exception;

public class EvidenceNotFoundException extends RuntimeException {

    public EvidenceNotFoundException(String message) {
        super(String.format("Evidence with provided id not exist: %s", message));
    }
}
