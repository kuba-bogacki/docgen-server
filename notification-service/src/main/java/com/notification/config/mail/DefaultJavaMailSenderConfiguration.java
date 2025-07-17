package com.notification.config.mail;

import com.notification.exception.SendEmailException;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.notification.util.ApplicationConstants.COMPANY_DOCUMENT_CREATOR;

@Slf4j
@Component
@RequiredArgsConstructor
public class DefaultJavaMailSenderConfiguration implements JavaMailSenderConfiguration {

    @Lazy
    private final JavaMailSender javaMailSender;

    @Override
    public void sendEmail(String from, String to, String subject, String body) {
        try {
            var message = javaMailSender.createMimeMessage();

            var helper = new MimeMessageHelper(message);
            helper.setFrom(from, COMPANY_DOCUMENT_CREATOR);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);

            javaMailSender.send(message);
        } catch (MessagingException | IOException exception) {
            var message = String.format("Error while sending email to %s file", to);
            log.error(message, exception);
            throw new SendEmailException(message);
        }
    }
}
