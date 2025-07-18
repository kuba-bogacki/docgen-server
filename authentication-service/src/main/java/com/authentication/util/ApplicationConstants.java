package com.authentication.util;

import org.springframework.stereotype.Component;

@Component
public class ApplicationConstants {

    public static final String API_VERSION = "/v1.0";
    public static final String PROTOCOL = "http";
    public static final String VALID_TOKEN = "Token is valid";
    public static final String EMAIL_PATTERN = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
}
