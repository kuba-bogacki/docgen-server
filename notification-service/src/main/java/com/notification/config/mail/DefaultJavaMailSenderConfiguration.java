package com.notification.config.mail;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.notification.util.ApplicationConstants.COMPANY_DOCUMENT_CREATOR;

@Component
@RequiredArgsConstructor
public class DefaultJavaMailSenderConfiguration implements JavaMailSenderConfiguration {

    @Lazy
    private final JavaMailSender javaMailSender;

    @Override
    public void sendEmail(String from, String to, String subject, String body) throws MessagingException, IOException {
        var message = javaMailSender.createMimeMessage();

        var helper = new MimeMessageHelper(message);
        helper.setFrom(from, COMPANY_DOCUMENT_CREATOR);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body, true);

        javaMailSender.send(message);
    }
}
