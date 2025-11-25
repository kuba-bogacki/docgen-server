package com.authentication.exception;

public class UserPaymentSessionException extends RuntimeException {

    public UserPaymentSessionException(String message) {
        super(String.format("Stripe payment intent error: %s", message));
    }
}
