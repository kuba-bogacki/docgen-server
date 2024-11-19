package com.notification.service;

import com.notification.config.mail.JavaMailSenderConfiguration;
import com.notification.config.reader.FileReaderConfiguration;
import com.notification.exception.ReadEmailContentException;
import com.notification.model.dto.CompanyDto;
import com.notification.model.dto.UserDto;
import com.notification.service.implementation.EmailServiceImplementation;
import jakarta.mail.MessagingException;
import lombok.SneakyThrows;
import org.apache.commons.lang.StringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static com.notification.util.ApplicationConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceImplementationTest extends NotificationSamples {

    @Captor private ArgumentCaptor<String> bodyCaptor;
    @Captor private ArgumentCaptor<String> emailCaptor;
    @SuppressWarnings("rawtypes")
    @Mock private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
    @SuppressWarnings("rawtypes")
    @Mock private WebClient.RequestHeadersSpec requestHeadersSpec;
    @Mock private WebClient.ResponseSpec responseSpec;
    @Mock private WebClient.Builder webClientBuilder;
    @Mock private WebClient webClient;
    @Mock private JavaMailSenderConfiguration javaMailSender;
    @Mock private FileReaderConfiguration fileReaderConfiguration;
    @InjectMocks private EmailServiceImplementation emailService;

    @Test
    @SneakyThrows
    @DisplayName("Should send an verification email if user dto is provided")
    void test_01() {
        //given
        final var sampleEmailUrl = PROTOCOL + "://" + CLIENT_ADDRESS + StringUtils.EMPTY + "/sign-in?verify-code=";
        final var sampleEmailMessage = sampleUserDto.getUserFirstNameI() + " " + sampleUserDto.getUserLastNameI();
        final var sampleBody = sampleEmailUrl + "\n" + sampleEmailMessage;

        //when
        when(fileReaderConfiguration.emailFormatterAndReader(REGISTRATION_EMAIL_FILE_NAME)).thenReturn(sampleBody);
        doNothing().when(javaMailSender).sendEmail(FOR_COMPANY_EMAIL_ADDRESS, userEmail, REGISTRATION_SUBJECT, sampleBody);

        emailService.sendVerificationEmail(sampleUserDto);

        //then
        verify(fileReaderConfiguration).emailFormatterAndReader(eq(REGISTRATION_EMAIL_FILE_NAME));
        verify(javaMailSender).sendEmail(eq(FOR_COMPANY_EMAIL_ADDRESS), emailCaptor.capture(), eq(REGISTRATION_SUBJECT), bodyCaptor.capture());
        assertThat(sampleUserDto.getUserEmail())
                .isNotNull()
                .isEqualTo(emailCaptor.getValue());
        assertThat(bodyCaptor.getValue())
                .isNotNull()
                .contains(sampleEmailUrl);
    }

    @Test
    @SneakyThrows
    @DisplayName("Should throw an exception if email formatter error occur due sending verification email")
    void test_02() {
        //given
        final var exceptionMessage = "Couldn't read email message from verify-email.txt file";

        //when
        when(fileReaderConfiguration.emailFormatterAndReader(REGISTRATION_EMAIL_FILE_NAME)).thenThrow(new ReadEmailContentException(exceptionMessage));

        final var expectedException = catchThrowable(() -> emailService.sendVerificationEmail(sampleUserDto));

        //then
        verify(fileReaderConfiguration).emailFormatterAndReader(eq(REGISTRATION_EMAIL_FILE_NAME));
        verify(javaMailSender, never()).sendEmail(anyString(), anyString(), anyString(), anyString());
        assertThat(expectedException)
                .isNotNull()
                .isInstanceOf(ReadEmailContentException.class)
                .hasMessageContaining(exceptionMessage);
    }

    @Test
    @SneakyThrows
    @DisplayName("Should throw an exception if email sender error occur due sending verification email")
    void test_03() {
        //given
        final var sampleEmailUrl = PROTOCOL + "://" + CLIENT_ADDRESS + StringUtils.EMPTY + "/sign-in?verify-code=";
        final var sampleEmailMessage = sampleUserDto.getUserFirstNameI() + " " + sampleUserDto.getUserLastNameI();
        final var sampleBody = sampleEmailUrl + "\n" + sampleEmailMessage;

        //when
        when(fileReaderConfiguration.emailFormatterAndReader(REGISTRATION_EMAIL_FILE_NAME)).thenReturn(sampleBody);
        doThrow(MessagingException.class).when(javaMailSender).sendEmail(anyString(), anyString(), anyString(), anyString());

        final var expectedException = catchThrowable(() -> emailService.sendVerificationEmail(sampleUserDto));

        //then
        verify(fileReaderConfiguration).emailFormatterAndReader(eq(REGISTRATION_EMAIL_FILE_NAME));
        verify(javaMailSender).sendEmail(anyString(), anyString(), anyString(), anyString());
        assertThat(expectedException)
                .isNotNull()
                .isInstanceOf(MessagingException.class);
    }

    @Test
    @SneakyThrows
    @DisplayName("Should send an email to reset password if user dto is provided")
    void test_04() {
        //given
        final var sampleEmailUrl = PROTOCOL + "://" + CLIENT_ADDRESS + StringUtils.EMPTY + "/change-password?user-verification-code=";
        final var sampleEmailMessage = sampleUserDto.getUserFirstNameI() + " " + sampleUserDto.getUserLastNameI();
        final var sampleBody = sampleEmailUrl + "\n" + sampleEmailMessage;

        //when
        when(fileReaderConfiguration.emailFormatterAndReader(RESET_PASSWORD_EMAIL_FILE_NAME)).thenReturn(sampleBody);
        doNothing().when(javaMailSender).sendEmail(FOR_COMPANY_EMAIL_ADDRESS, userEmail, RESET_PASSWORD_SUBJECT, sampleBody);

        emailService.sendResetPasswordEmail(sampleUserDto);

        //then
        verify(fileReaderConfiguration).emailFormatterAndReader(eq(RESET_PASSWORD_EMAIL_FILE_NAME));
        verify(javaMailSender).sendEmail(eq(FOR_COMPANY_EMAIL_ADDRESS), emailCaptor.capture(), eq(RESET_PASSWORD_SUBJECT), bodyCaptor.capture());
        assertThat(sampleUserDto.getUserEmail())
                .isNotNull()
                .isEqualTo(emailCaptor.getValue());
        assertThat(bodyCaptor.getValue())
                .isNotNull()
                .contains(sampleEmailUrl);
    }

    @Test
    @SneakyThrows
    @DisplayName("Should throw an exception if email formatter error occur due sending email to reset password")
    void test_05() {
        //given
        final var exceptionMessage = "Couldn't read email message from verify-email.txt file";

        //when
        when(fileReaderConfiguration.emailFormatterAndReader(RESET_PASSWORD_EMAIL_FILE_NAME)).thenThrow(new ReadEmailContentException(exceptionMessage));

        final var expectedException = catchThrowable(() -> emailService.sendResetPasswordEmail(sampleUserDto));

        //then
        verify(fileReaderConfiguration).emailFormatterAndReader(eq(RESET_PASSWORD_EMAIL_FILE_NAME));
        verify(javaMailSender, never()).sendEmail(anyString(), anyString(), anyString(), anyString());
        assertThat(expectedException)
                .isNotNull()
                .isInstanceOf(ReadEmailContentException.class)
                .hasMessageContaining(exceptionMessage);
    }

    @Test
    @SneakyThrows
    @DisplayName("Should throw an exception if email sender error occur due sending email to reset password")
    void test_06() {
        //given
        final var sampleEmailUrl = PROTOCOL + "://" + CLIENT_ADDRESS + StringUtils.EMPTY + "/change-password?user-verification-code=";
        final var sampleEmailMessage = sampleUserDto.getUserFirstNameI() + " " + sampleUserDto.getUserLastNameI();
        final var sampleBody = sampleEmailUrl + "\n" + sampleEmailMessage;

        //when
        when(fileReaderConfiguration.emailFormatterAndReader(RESET_PASSWORD_EMAIL_FILE_NAME)).thenReturn(sampleBody);
        doThrow(MessagingException.class).when(javaMailSender).sendEmail(FOR_COMPANY_EMAIL_ADDRESS, userEmail, RESET_PASSWORD_SUBJECT, sampleBody);

        final var expectedException = catchThrowable(() -> emailService.sendResetPasswordEmail(sampleUserDto));

        //then
        verify(fileReaderConfiguration).emailFormatterAndReader(eq(RESET_PASSWORD_EMAIL_FILE_NAME));
        verify(javaMailSender).sendEmail(eq(FOR_COMPANY_EMAIL_ADDRESS), emailCaptor.capture(), eq(RESET_PASSWORD_SUBJECT), bodyCaptor.capture());
        assertThat(expectedException)
                .isNotNull()
                .isInstanceOf(MessagingException.class);
    }

    @Test
    @SuppressWarnings("unchecked")
    @SneakyThrows
    @DisplayName("Should send an invitation email if invitation dto and jwt token are provided")
    void test_07() {
        //given
        final var sampleJwtToken = "sampleJwtToken";
        final var sampleUserUri = "http://authentication-service/v1.0/authentication/user";
        final var sampleCompanyUri = "http://company-service/v1.0/company/details/" + companyId;
        final var sampleEmailUrl = PROTOCOL + "://" + CLIENT_ADDRESS + StringUtils.EMPTY + "/sign-in?join-to-company=";
        final var sampleEmailMessage = sampleUserDto.getUserFirstNameI() + " " + sampleUserDto.getUserLastNameI();
        final var sampleBody = sampleEmailUrl + "\n" + sampleEmailMessage;

        //when
        when(fileReaderConfiguration.emailFormatterAndReader(USER_INVITATION_EMAIL_FILE_NAME)).thenReturn(sampleBody);
        doNothing().when(javaMailSender).sendEmail(FOR_COMPANY_EMAIL_ADDRESS, userEmail, USER_INVITATION_SUBJECT, sampleBody);

        when(webClientBuilder.filter(any())).thenReturn(webClientBuilder);
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(sampleUserUri)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(UserDto.class)).thenReturn(Mono.just(sampleUserDto));

        when(webClientBuilder.filter(any())).thenReturn(webClientBuilder);
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(sampleCompanyUri)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(CompanyDto.class)).thenReturn(Mono.just(sampleCompanyDto));

        emailService.sendInvitationEmail(sampleInvitationDto, sampleJwtToken);

        //then
        verify(fileReaderConfiguration).emailFormatterAndReader(eq(USER_INVITATION_EMAIL_FILE_NAME));
        verify(javaMailSender).sendEmail(eq(FOR_COMPANY_EMAIL_ADDRESS), emailCaptor.capture(), eq(USER_INVITATION_SUBJECT), bodyCaptor.capture());
        assertThat(sampleUserDto.getUserEmail())
                .isNotNull()
                .isEqualTo(emailCaptor.getValue());
        assertThat(bodyCaptor.getValue())
                .isNotNull()
                .contains(sampleEmailUrl);
    }
}