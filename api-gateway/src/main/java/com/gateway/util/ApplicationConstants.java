package com.gateway.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ApplicationConstants {

    public static final String ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";
    public static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
    public static final String ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";
    public static final String ACCESS_CONTROL_ALLOW_CREDENTIALS = "Access-Control-Allow-Credentials";
    public static final String ALLOWED_HEADERS = "Authorization, Content-Type";
    public static final String ALLOWED_METHODS = "GET, PUT, POST, DELETE, OPTIONS, PATCH";
    public static final String ALLOWED_ORIGIN = "http://localhost:3000";
    public static final String ALLOWED_CREDENTIALS = "true";
    public static final String PROTOCOL = "http";
    public static final String COOKIE = "Cookie";
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String USER_EMAIL_HEADER = "X-User-Email";
    public static final String USER_ROLE_HEADER = "X-User-Role";
    public static final String SUB = "sub";
    public static final String ROLE = "role";
}
