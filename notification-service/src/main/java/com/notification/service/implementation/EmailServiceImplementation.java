package com.notification.service.implementation;

import com.notification.infrastructure.HttpClient;
import com.notification.client.mail.JavaMailSenderClient;
import com.notification.client.reader.FileReaderClient;
import com.notification.exception.InvitationSendFailureException;
import com.notification.model.dto.InvitationDto;
import com.notification.model.dto.UserDto;
import com.notification.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static com.notification.util.ApplicationConstants.*;
import static com.notification.util.HttpClientUtil.buildUrl;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImplementation implements EmailService {

    private final HttpClient httpClient;
    private final FileReaderClient fileReaderClient;
    private final JavaMailSenderClient javaMailSender;

    @Override
    public void sendVerificationEmail(UserDto userDto) {

        var url = buildUrl(PROTOCOL, CLIENT_ADDRESS, StringUtils.EMPTY, "/sign-in?verify-code=", userDto.getUserVerificationCode());
        var body = fileReaderClient.emailFormatterAndReader(REGISTRATION_EMAIL_FILE_NAME)
                .replace("[[name]]", userDto.getUserFirstNameI() + " " + userDto.getUserLastNameI())
                .replace("[[URL]]", url);

        javaMailSender.sendEmail(FOR_COMPANY_EMAIL_ADDRESS, userDto.getUserEmail(), REGISTRATION_SUBJECT, body);
        log.debug("Verification email sent to {}", userDto.getUserEmail());
    }

    @Override
    public void sendResetPasswordEmail(UserDto userDto) {

        var url = buildUrl(PROTOCOL,CLIENT_ADDRESS, StringUtils.EMPTY,"/change-password?user-verification-code=",
                userDto.getUserVerificationCode(), "&user-email=", userDto.getUserEmail());
        var body = fileReaderClient.emailFormatterAndReader(RESET_PASSWORD_EMAIL_FILE_NAME)
                .replace("[[name]]", userDto.getUserFirstNameI() + " " + userDto.getUserLastNameI())
                .replace("[[URL]]", url);

        javaMailSender.sendEmail(FOR_COMPANY_EMAIL_ADDRESS, userDto.getUserEmail(), RESET_PASSWORD_SUBJECT, body);
        log.debug("Reset password email sent to {}", userDto.getUserEmail());
    }

    @Override
    public void sendInvitationEmail(InvitationDto invitationDto, String userEmail) {
        final var currentUserDto = httpClient.getCurrentUserDto(userEmail);
        final var currentCompanyDto = httpClient.getCompanyDtoById(invitationDto.getCompanyId(), userEmail);

        if (Objects.isNull(currentUserDto) || Objects.isNull(currentCompanyDto)) {
            throw new InvitationSendFailureException("Impossible to send invitation - current user or current company is null");
        }

        var url = buildUrl(PROTOCOL, CLIENT_ADDRESS, StringUtils.EMPTY, "/sign-in?join-to-company=", invitationDto.getCompanyId());
        var body = fileReaderClient.emailFormatterAndReader(USER_INVITATION_EMAIL_FILE_NAME)
                .replace("[[name]]", invitationDto.getUserEmail())
                .replace("[[company]]", currentCompanyDto.getCompanyName())
                .replace("[[URL]]", url);

        javaMailSender.sendEmail(FOR_COMPANY_EMAIL_ADDRESS, invitationDto.getUserEmail(), USER_INVITATION_SUBJECT, body);
        log.debug("Invitation email sent to {}", invitationDto.getUserEmail());
    }
}
