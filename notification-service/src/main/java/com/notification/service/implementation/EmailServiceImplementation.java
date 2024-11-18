package com.notification.service.implementation;

import com.notification.config.mail.JavaMailSenderConfiguration;
import com.notification.config.reader.FileReaderConfiguration;
import com.notification.exception.InvitationSendFailureException;
import com.notification.model.dto.CompanyDto;
import com.notification.model.dto.InvitationDto;
import com.notification.model.dto.UserDto;
import com.notification.service.EmailService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.Objects;

import static com.notification.util.ApplicationConstants.*;
import static com.notification.util.UrlBuilder.addTokenHeader;
import static com.notification.util.UrlBuilder.buildUrl;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImplementation implements EmailService {

    private final JavaMailSenderConfiguration javaMailSender;
    private final FileReaderConfiguration fileReaderConfiguration;
    private final WebClient.Builder webClientBuilder;

    @Override
    public void sendVerificationEmail(UserDto userDto) throws MessagingException, IOException {

        var url = buildUrl(PROTOCOL, CLIENT_ADDRESS, StringUtils.EMPTY, "/sign-in?verify-code=", userDto.getUserVerificationCode());
        var body = fileReaderConfiguration.emailFormatterAndReader(REGISTRATION_EMAIL_FILE_NAME)
                .replace("[[name]]", userDto.getUserFirstNameI() + " " + userDto.getUserLastNameI())
                .replace("[[URL]]", url);

        javaMailSender.sendEmail(FOR_COMPANY_EMAIL_ADDRESS, userDto.getUserEmail(), REGISTRATION_SUBJECT, body);
        log.debug("Verification email sent to {}", userDto.getUserEmail());
    }

    @Override
    public void sendResetPasswordEmail(UserDto userDto) throws MessagingException, IOException {

        var url = buildUrl(PROTOCOL,CLIENT_ADDRESS, StringUtils.EMPTY,"/change-password?user-verification-code=",
                userDto.getUserVerificationCode(), "&user-email=", userDto.getUserEmail());
        var body = fileReaderConfiguration.emailFormatterAndReader(RESET_PASSWORD_EMAIL_FILE_NAME)
                .replace("[[name]]", userDto.getUserFirstNameI() + " " + userDto.getUserLastNameI())
                .replace("[[URL]]", url);

        javaMailSender.sendEmail(FOR_COMPANY_EMAIL_ADDRESS, userDto.getUserEmail(), RESET_PASSWORD_SUBJECT, body);
        log.debug("Reset password email sent to {}", userDto.getUserEmail());
    }

    @Override
    public void sendInvitationEmail(InvitationDto invitationDto, String jwtToken) throws MessagingException, IOException {
        final var currentUserDto = getCurrentUserDto(jwtToken);
        final var currentCompanyDto = getCurrentCompanyDto(jwtToken, invitationDto.getCompanyId());

        if (Objects.isNull(currentUserDto) || Objects.isNull(currentCompanyDto)) {
            throw new InvitationSendFailureException("Impossible to send invitation - current user or current company is null");
        }

        var url = buildUrl(PROTOCOL, CLIENT_ADDRESS, StringUtils.EMPTY, "/sign-in?join-to-company=", invitationDto.getCompanyId());
        var body = fileReaderConfiguration.emailFormatterAndReader(USER_INVITATION_EMAIL_FILE_NAME)
                .replace("[[name]]", invitationDto.getUserEmail())
                .replace("[[company]]", currentCompanyDto.getCompanyName())
                .replace("[[URL]]", url);

        javaMailSender.sendEmail(FOR_COMPANY_EMAIL_ADDRESS, invitationDto.getUserEmail(), USER_INVITATION_SUBJECT, body);
        log.debug("Invitation email sent to {}", invitationDto.getUserEmail());
    }

    private UserDto getCurrentUserDto(String jwtToken) {
        return webClientBuilder
                .filter(addTokenHeader(jwtToken))
                .build().get()
                .uri(buildUrl(PROTOCOL, "authentication-service", API_VERSION, "/authentication/user"))
                .retrieve()
                .bodyToMono(UserDto.class)
                .block();
    }

    private CompanyDto getCurrentCompanyDto(String jwtToken, String companyId) {
        return webClientBuilder
                .filter(addTokenHeader(jwtToken))
                .build().get()
                .uri(buildUrl(PROTOCOL, "company-service", API_VERSION, "/company/details/" + companyId))
                .retrieve()
                .bodyToMono(CompanyDto.class)
                .block();
    }
}
