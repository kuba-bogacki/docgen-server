package com.notification.config.mail;

public interface JavaMailSenderConfiguration {
    void sendEmail(String from, String to, String subject, String body);
}
