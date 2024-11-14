package com.notification.service.implementation;

import com.notification.exception.InvitationSendFailureException;
import com.notification.model.dto.CompanyDto;
import com.notification.model.dto.InvitationDto;
import com.notification.model.dto.UserDto;
import com.notification.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;

import static com.notification.util.ApplicationConstants.*;
import static com.notification.util.UrlBuilder.addTokenHeader;
import static com.notification.util.UrlBuilder.buildUrl;
import static java.nio.charset.StandardCharsets.ISO_8859_1;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImplementation implements EmailService {

    @Lazy
    private final JavaMailSender javaMailSender;
    private final WebClient.Builder webClientBuilder;

    private String fileFormatterAndReader(String fileName) {
        var filePath = STATIC_FILE_FOLDER + fileName;

        try (var reader = new InputStreamReader(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(filePath)), ISO_8859_1);
             BufferedReader bufferedReader = new BufferedReader(reader);
        ) {
            String line;
            StringBuilder emailBody = new StringBuilder();

            while ((line = bufferedReader.readLine()) != null) {
                emailBody.append(line);
            }
            return emailBody.toString();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new NotFoundException(e);
        }
    }

    @Override
    @Transactional
    public void sendVerificationEmail(UserDto userDto) throws MessagingException, IOException {

        String content = fileFormatterAndReader("verify-email.txt");

        StringBuilder verifyUrlStringBuilder = new StringBuilder();

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setFrom(FOR_COMPANY_EMAIL_ADDRESS, "Company document creator");
        helper.setTo(userDto.getUserEmail());
        helper.setSubject("Dear user, verify your registration");
        content = content.replace("[[name]]", userDto.getUserFirstNameI() + " " + userDto.getUserLastNameI());
        verifyUrlStringBuilder
                .append(PROTOCOL)
                .append("://")
                .append(CLIENT_ADDRESS)
                .append("/sign-in?verify-code=")
                .append(userDto.getUserVerificationCode());
        content = content.replace("[[URL]]", verifyUrlStringBuilder.toString());
        helper.setText(content, true);

        javaMailSender.send(message);
    }

    @Override
    @Transactional
    public void sendResetPasswordEmail(UserDto userDto) throws MessagingException, IOException {

        String content = fileFormatterAndReader("reset-password.txt");

        StringBuilder verifyUrlStringBuilder = new StringBuilder();

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setFrom(FOR_COMPANY_EMAIL_ADDRESS, "Company document creator");
        helper.setTo(userDto.getUserEmail());
        helper.setSubject("Dear user, now you can reset your password");
        content = content.replace("[[name]]", userDto.getUserFirstNameI() + " " + userDto.getUserLastNameI());
        verifyUrlStringBuilder
                .append(PROTOCOL)
                .append("://")
                .append(CLIENT_ADDRESS)
                .append("/change-password?user-verification-code=")
                .append(userDto.getUserVerificationCode())
                .append("&user-email=")
                .append(userDto.getUserEmail());
        content = content.replace("[[URL]]", verifyUrlStringBuilder.toString());
        helper.setText(content, true);

        javaMailSender.send(message);
    }

    @Override
    @Transactional
    public void sendInvitationEmail(InvitationDto invitationDto, String jwtToken) throws MessagingException, IOException {
        UserDto currentUserDto = webClientBuilder
                .filter(addTokenHeader(jwtToken))
                .build().get()
                .uri(buildUrl(PROTOCOL, "authentication-service", API_VERSION, "/authentication/user"))
                .retrieve()
                .bodyToMono(UserDto.class)
                .block();

        CompanyDto currentCompanyDto = webClientBuilder
                .filter(addTokenHeader(jwtToken))
                .build().get()
                .uri(buildUrl(PROTOCOL, "company-service", API_VERSION, "/company/details/" + invitationDto.getCompanyId()))
                .retrieve()
                .bodyToMono(CompanyDto.class)
                .block();

        if (Objects.isNull(currentUserDto) || Objects.isNull(currentCompanyDto)) {
            throw new InvitationSendFailureException("Impossible to send invitation - current user or current company is null");
        }

        String content = fileFormatterAndReader("user-invitation.txt");

        StringBuilder verifyUrlStringBuilder = new StringBuilder();

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setFrom(FOR_COMPANY_EMAIL_ADDRESS, "Company document creator");
        helper.setTo(invitationDto.getUserEmail());
        helper.setSubject("You've been invited to company");
        content = content.replace("[[name]]", invitationDto.getUserEmail()).replace("[[company]]", currentCompanyDto.getCompanyName());
        verifyUrlStringBuilder
                .append(PROTOCOL)
                .append("://")
                .append(CLIENT_ADDRESS)
                .append("/sign-in?join-to-company=")
                .append(invitationDto.getCompanyId());
        content = content.replace("[[URL]]", verifyUrlStringBuilder.toString());
        helper.setText(content, true);

        javaMailSender.send(message);
    }
}
