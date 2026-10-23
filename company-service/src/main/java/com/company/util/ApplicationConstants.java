package com.company.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ApplicationConstants {

    // Url
    public static final String HTTP = "http";
    public static final String API_VERSION = "/api/v1.0";

    // Header
    public static final String USER_EMAIL_HEADER = "X-User-Email";
    public static final String USER_ROLE_HEADER = "X-User-Role";

    // Service
    public static final String AUTHENTICATION_SERVICE = "authentication-service";
}
