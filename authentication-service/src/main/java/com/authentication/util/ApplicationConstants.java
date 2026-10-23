package com.authentication.util;

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

    // Param
    public static final String LOADED_IMAGE_PARAM = "loadedImage";
    public static final String USER_EMAIL_PARAM = "userEmail";

    // Regex
    public static final String EMAIL_PATTERN = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
}
