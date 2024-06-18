package com.notification.service.implementation;

import com.notification.model.dto.CompanyDto;
import com.notification.model.dto.InvitationDto;
import com.notification.model.dto.UserDto;
import com.notification.exception.InvitationSendFailureException;
import com.notification.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static com.notification.util.ApplicationConstants.*;
import static com.notification.util.UrlBuilder.addTokenHeader;
import static com.notification.util.UrlBuilder.buildUrl;

@Service
@RequiredArgsConstructor
class EmailServiceImplementation implements EmailService {

    @Lazy
    private final JavaMailSender javaMailSender;
    private final WebClient.Builder webClientBuilder;

    public String fileFormatterAndReader(String text) throws IOException {

        File fileDirectory = new File(text);
        FileInputStream fileInputStream = new FileInputStream(fileDirectory);
        Charset inputCharset = StandardCharsets.ISO_8859_1;
        BufferedReader in = new BufferedReader(new InputStreamReader(fileInputStream, inputCharset));

        String line;
        StringBuilder emailBody = new StringBuilder();

        while ((line = in.readLine()) != null) {
            emailBody.append(line);
        }
        return emailBody.toString();
    }

    @Override
    @Transactional
    public void sendVerificationEmail(UserDto userDto) throws MessagingException, IOException {

        String textRoute = NOTIFICATION_ROUTE + "/verify-email.txt";
        String content = fileFormatterAndReader(textRoute);

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setFrom("for.company.document.generator@gmail.com", "Company document creator");
        helper.setTo(userDto.getUserEmail());
        helper.setSubject("Dear user, verify your registration");
        content = content.replace("[[name]]", userDto.getUserFirstNameI() + " " + userDto.getUserLastNameI());
        String verifyURL = PROTOCOL + "://" + CLIENT_ADDRESS + "/sign-in?verify-code=" + userDto.getUserVerificationCode();
        content = content.replace("[[URL]]", verifyURL);
        helper.setText(content, true);

        javaMailSender.send(message);
    }

    @Override
    @Transactional
    public void sendResetPasswordEmail(UserDto userDto) throws MessagingException, IOException {

        String textRoute = NOTIFICATION_ROUTE + "/reset-password.txt";
        String content = fileFormatterAndReader(textRoute);

        StringBuilder verifyUrlStringBuilder = new StringBuilder();

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setFrom("for.company.document.generator@gmail.com", "Company document creator");
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

        String textRoute = NOTIFICATION_ROUTE + "/user-invitation.txt";
        String content = fileFormatterAndReader(textRoute);

        StringBuilder verifyUrlStringBuilder = new StringBuilder();

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setFrom("for.company.document.generator@gmail.com", "Company document creator");
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
