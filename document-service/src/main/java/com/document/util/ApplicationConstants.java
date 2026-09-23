package com.document.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ApplicationConstants {

    public static final String API_VERSION = "/v1.0";
    public static final String PROTOCOL = "http";
    public static final String USER_EMAIL_HEADER = "X-User-Email";
    public static final String USER_ROLE_HEADER = "X-User-Role";
    public static final String STATIC_FILE_FOLDER = "static/";
    public static final String DEFAULT_DATE_PATTERN = "dd-MM-yyyy";
    public static final String FINANCIAL_STATEMENT_FILE_NAME = "financial-statement.docx";
}
