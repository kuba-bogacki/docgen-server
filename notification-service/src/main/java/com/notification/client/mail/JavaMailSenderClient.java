package com.notification.client.mail;

public interface JavaMailSenderClient {
    void sendEmail(String from, String to, String subject, String body);
}
