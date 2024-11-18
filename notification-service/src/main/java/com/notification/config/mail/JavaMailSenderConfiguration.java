package com.notification.config.mail;

import jakarta.mail.MessagingException;

import java.io.IOException;

public interface JavaMailSenderConfiguration {
    void sendEmail(String from, String to, String subject, String body) throws MessagingException, IOException;
}
