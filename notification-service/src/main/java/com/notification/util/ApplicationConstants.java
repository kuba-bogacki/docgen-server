package com.notification.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ApplicationConstants {

    // Url
    public static final String HTTP = "http";
    public static final String API_VERSION = "/api/v1.0";

    // Header
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String USER_EMAIL_HEADER = "X-User-Email";
    public static final String ALLOWED_ORIGIN_PATTERN = "*";
    public static final String BEARER = "Bearer ";
    public static final String SUB = "sub";

    // Email
    public static final String FOR_COMPANY_EMAIL_ADDRESS = "for.company.document.generator@gmail.com";

    // File
    public static final String STATIC_FILE_FOLDER = "static/";
    public static final String REGISTRATION_EMAIL_FILE_NAME = "verify-email.txt";
    public static final String RESET_PASSWORD_EMAIL_FILE_NAME = "reset-password.txt";
    public static final String USER_INVITATION_EMAIL_FILE_NAME = "user-invitation.txt";

    // Message
    public static final String COMPANY_DOCUMENT_CREATOR = "Company document creator";
    public static final String USER_INVITATION_SUBJECT = "You've been invited to company";
    public static final String REGISTRATION_SUBJECT = "Dear user, verify your registration";
    public static final String RESET_PASSWORD_SUBJECT = "Dear user, now you can reset your password";
}
