package com.notification.service.implementation;

import com.notification.client.mail.JavaMailSenderClient;
import com.notification.client.reader.FileReaderClient;
import com.notification.exception.InvitationSendFailureException;
import com.notification.exception.ReadEmailContentException;
import com.notification.exception.SendEmailException;
import com.notification.infrastructure.HttpClient;
import org.apache.commons.lang.StringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.notification.util.ApplicationConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmailServiceImplementationTest extends NotificationSamples {

    @Captor private ArgumentCaptor<String> bodyCaptor;
    @Captor private ArgumentCaptor<String> emailCaptor;
    @Mock private HttpClient httpClient;
    @Mock private JavaMailSenderClient javaMailSender;
    @Mock private FileReaderClient fileReaderClient;
    @InjectMocks private EmailServiceImplementation emailService;

    @Test
    @DisplayName("Should send an verification email if user dto is provided")
    void test_01() {
        //given
        final var sampleEmailUrl = PROTOCOL + "://" + CLIENT_ADDRESS + StringUtils.EMPTY + "/sign-in?verify-code=";
        final var sampleEmailMessage = sampleUserDto.getUserFirstNameI() + " " + sampleUserDto.getUserLastNameI();
        final var sampleBody = sampleEmailUrl + "\n" + sampleEmailMessage;

        //when
        when(fileReaderClient.emailFormatterAndReader(REGISTRATION_EMAIL_FILE_NAME)).thenReturn(sampleBody);
        doNothing().when(javaMailSender).sendEmail(FOR_COMPANY_EMAIL_ADDRESS, userEmail, REGISTRATION_SUBJECT, sampleBody);

        emailService.sendVerificationEmail(sampleUserDto);

        //then
        verify(fileReaderClient).emailFormatterAndReader(eq(REGISTRATION_EMAIL_FILE_NAME));
        verify(javaMailSender).sendEmail(eq(FOR_COMPANY_EMAIL_ADDRESS), emailCaptor.capture(), eq(REGISTRATION_SUBJECT), bodyCaptor.capture());
        assertThat(sampleUserDto.getUserEmail())
                .isNotNull()
                .isEqualTo(emailCaptor.getValue());
        assertThat(bodyCaptor.getValue())
                .isNotNull()
                .contains(sampleEmailUrl);
    }

    @Test
    @DisplayName("Should throw an exception if email formatter error occur due sending verification email")
    void test_02() {
        //given
        final var exceptionMessage = "Couldn't read email message from verify-email.txt file";

        //when
        when(fileReaderClient.emailFormatterAndReader(REGISTRATION_EMAIL_FILE_NAME)).thenThrow(new ReadEmailContentException(exceptionMessage));

        final var expectedException = catchThrowable(() -> emailService.sendVerificationEmail(sampleUserDto));

        //then
        verify(fileReaderClient).emailFormatterAndReader(eq(REGISTRATION_EMAIL_FILE_NAME));
        verify(javaMailSender, never()).sendEmail(anyString(), anyString(), anyString(), anyString());
        assertThat(expectedException)
                .isNotNull()
                .isInstanceOf(ReadEmailContentException.class)
                .hasMessageContaining(exceptionMessage);
    }

    @Test
    @DisplayName("Should throw an exception if email sender error occur due sending verification email")
    void test_03() {
        //given
        final var sampleEmailUrl = PROTOCOL + "://" + CLIENT_ADDRESS + StringUtils.EMPTY + "/sign-in?verify-code=";
        final var sampleEmailMessage = sampleUserDto.getUserFirstNameI() + " " + sampleUserDto.getUserLastNameI();
        final var sampleBody = sampleEmailUrl + "\n" + sampleEmailMessage;

        //when
        when(fileReaderClient.emailFormatterAndReader(REGISTRATION_EMAIL_FILE_NAME)).thenReturn(sampleBody);
        doThrow(SendEmailException.class).when(javaMailSender).sendEmail(anyString(), anyString(), anyString(), anyString());

        final var expectedException = catchThrowable(() -> emailService.sendVerificationEmail(sampleUserDto));

        //then
        verify(fileReaderClient).emailFormatterAndReader(eq(REGISTRATION_EMAIL_FILE_NAME));
        verify(javaMailSender).sendEmail(anyString(), anyString(), anyString(), anyString());
        assertThat(expectedException)
                .isNotNull()
                .isInstanceOf(SendEmailException.class);
    }

    @Test
    @DisplayName("Should send an email to reset password if user dto is provided")
    void test_04() {
        //given
        final var sampleEmailUrl = PROTOCOL + "://" + CLIENT_ADDRESS + StringUtils.EMPTY + "/change-password?user-verification-code=";
        final var sampleEmailMessage = sampleUserDto.getUserFirstNameI() + " " + sampleUserDto.getUserLastNameI();
        final var sampleBody = sampleEmailUrl + "\n" + sampleEmailMessage;

        //when
        when(fileReaderClient.emailFormatterAndReader(RESET_PASSWORD_EMAIL_FILE_NAME)).thenReturn(sampleBody);
        doNothing().when(javaMailSender).sendEmail(FOR_COMPANY_EMAIL_ADDRESS, userEmail, RESET_PASSWORD_SUBJECT, sampleBody);

        emailService.sendResetPasswordEmail(sampleUserDto);

        //then
        verify(fileReaderClient).emailFormatterAndReader(eq(RESET_PASSWORD_EMAIL_FILE_NAME));
        verify(javaMailSender).sendEmail(eq(FOR_COMPANY_EMAIL_ADDRESS), emailCaptor.capture(), eq(RESET_PASSWORD_SUBJECT), bodyCaptor.capture());
        assertThat(sampleUserDto.getUserEmail())
                .isNotNull()
                .isEqualTo(emailCaptor.getValue());
        assertThat(bodyCaptor.getValue())
                .isNotNull()
                .contains(sampleEmailUrl);
    }

    @Test
    @DisplayName("Should throw an exception if email formatter error occur due sending email to reset password")
    void test_05() {
        //given
        final var exceptionMessage = "Couldn't read email message from verify-email.txt file";

        //when
        when(fileReaderClient.emailFormatterAndReader(RESET_PASSWORD_EMAIL_FILE_NAME)).thenThrow(new ReadEmailContentException(exceptionMessage));

        final var expectedException = catchThrowable(() -> emailService.sendResetPasswordEmail(sampleUserDto));

        //then
        verify(fileReaderClient).emailFormatterAndReader(eq(RESET_PASSWORD_EMAIL_FILE_NAME));
        verify(javaMailSender, never()).sendEmail(anyString(), anyString(), anyString(), anyString());
        assertThat(expectedException)
                .isNotNull()
                .isInstanceOf(ReadEmailContentException.class)
                .hasMessageContaining(exceptionMessage);
    }

    @Test
    @DisplayName("Should throw an exception if email sender error occur due sending email to reset password")
    void test_06() {
        //given
        final var sampleEmailUrl = PROTOCOL + "://" + CLIENT_ADDRESS + StringUtils.EMPTY + "/change-password?user-verification-code=";
        final var sampleEmailMessage = sampleUserDto.getUserFirstNameI() + " " + sampleUserDto.getUserLastNameI();
        final var sampleBody = sampleEmailUrl + "\n" + sampleEmailMessage;

        //when
        when(fileReaderClient.emailFormatterAndReader(RESET_PASSWORD_EMAIL_FILE_NAME)).thenReturn(sampleBody);
        doThrow(SendEmailException.class).when(javaMailSender).sendEmail(FOR_COMPANY_EMAIL_ADDRESS, userEmail, RESET_PASSWORD_SUBJECT, sampleBody);

        final var expectedException = catchThrowable(() -> emailService.sendResetPasswordEmail(sampleUserDto));

        //then
        verify(fileReaderClient).emailFormatterAndReader(eq(RESET_PASSWORD_EMAIL_FILE_NAME));
        verify(javaMailSender).sendEmail(eq(FOR_COMPANY_EMAIL_ADDRESS), emailCaptor.capture(), eq(RESET_PASSWORD_SUBJECT), bodyCaptor.capture());
        assertThat(expectedException)
                .isNotNull()
                .isInstanceOf(SendEmailException.class);
    }

    @Test
    @DisplayName("Should send an invitation email if invitation dto and jwt token are provided")
    void test_07() {
        //given
        final var sampleEmailUrl = PROTOCOL + "://" + CLIENT_ADDRESS + StringUtils.EMPTY + "/sign-in?join-to-company=";
        final var sampleEmailMessage = sampleUserDto.getUserFirstNameI() + " " + sampleUserDto.getUserLastNameI();
        final var sampleBody = sampleEmailUrl + "\n" + sampleEmailMessage;

        //when
        when(fileReaderClient.emailFormatterAndReader(USER_INVITATION_EMAIL_FILE_NAME)).thenReturn(sampleBody);
        doNothing().when(javaMailSender).sendEmail(FOR_COMPANY_EMAIL_ADDRESS, userEmail, USER_INVITATION_SUBJECT, sampleBody);
        when(httpClient.getCurrentUserDto(userEmail)).thenReturn(sampleUserDto);
        when(httpClient.getCompanyDtoById(companyId, userEmail)).thenReturn(sampleCompanyDto);

        emailService.sendInvitationEmail(sampleInvitationDto, userEmail);

        //then
        verify(fileReaderClient).emailFormatterAndReader(eq(USER_INVITATION_EMAIL_FILE_NAME));
        verify(javaMailSender).sendEmail(eq(FOR_COMPANY_EMAIL_ADDRESS), emailCaptor.capture(), eq(USER_INVITATION_SUBJECT), bodyCaptor.capture());
        assertThat(sampleUserDto.getUserEmail())
                .isNotNull()
                .isEqualTo(emailCaptor.getValue());
        assertThat(bodyCaptor.getValue())
                .isNotNull()
                .contains(sampleEmailUrl);
    }

    @Test
    @DisplayName("Should thrown an exception if error occur due sending email")
    void test_08() {
        //given
        final var sampleEmailUrl = PROTOCOL + "://" + CLIENT_ADDRESS + StringUtils.EMPTY + "/sign-in?join-to-company=";
        final var sampleEmailMessage = sampleUserDto.getUserFirstNameI() + " " + sampleUserDto.getUserLastNameI();
        final var sampleBody = sampleEmailUrl + "\n" + sampleEmailMessage;

        //when
        when(fileReaderClient.emailFormatterAndReader(USER_INVITATION_EMAIL_FILE_NAME)).thenReturn(sampleBody);
        doThrow(SendEmailException.class).when(javaMailSender).sendEmail(FOR_COMPANY_EMAIL_ADDRESS, userEmail, USER_INVITATION_SUBJECT, sampleBody);
        when(httpClient.getCurrentUserDto(userEmail)).thenReturn(sampleUserDto);
        when(httpClient.getCompanyDtoById(companyId, userEmail)).thenReturn(sampleCompanyDto);

        final var expectedException = catchThrowable(() -> emailService.sendInvitationEmail(sampleInvitationDto, userEmail));

        //then
        verify(fileReaderClient).emailFormatterAndReader(eq(USER_INVITATION_EMAIL_FILE_NAME));
        verify(javaMailSender).sendEmail(eq(FOR_COMPANY_EMAIL_ADDRESS), emailCaptor.capture(), eq(USER_INVITATION_SUBJECT), bodyCaptor.capture());
        assertThat(sampleUserDto.getUserEmail())
                .isNotNull()
                .isEqualTo(emailCaptor.getValue());
        assertThat(bodyCaptor.getValue())
                .isNotNull()
                .contains(sampleEmailUrl);
        assertThat(expectedException)
                .isNotNull()
                .isInstanceOf(SendEmailException.class);
    }

    @Test
    @DisplayName("Should thrown an exception if error occur due reading and formatting an email")
    void test_09() {
        //when
        when(fileReaderClient.emailFormatterAndReader(USER_INVITATION_EMAIL_FILE_NAME)).thenThrow(ReadEmailContentException.class);
        when(httpClient.getCurrentUserDto(userEmail)).thenReturn(sampleUserDto);
        when(httpClient.getCompanyDtoById(companyId, userEmail)).thenReturn(sampleCompanyDto);

        final var expectedException = catchThrowable(() -> emailService.sendInvitationEmail(sampleInvitationDto, userEmail));

        //then
        verify(fileReaderClient).emailFormatterAndReader(eq(USER_INVITATION_EMAIL_FILE_NAME));
        verify(javaMailSender, never()).sendEmail(anyString(), anyString(), anyString(), anyString());
        assertThat(expectedException)
                .isNotNull()
                .isInstanceOf(ReadEmailContentException.class);
    }

    @Test
    @DisplayName("Should thrown an exception if error occur due getting current company dto")
    void test_10() {
        //when
        when(httpClient.getCurrentUserDto(userEmail)).thenReturn(sampleUserDto);
        when(httpClient.getCompanyDtoById(companyId, userEmail)).thenReturn(null);

        final var expectedException = catchThrowable(() -> emailService.sendInvitationEmail(sampleInvitationDto, userEmail));

        //then
        verify(fileReaderClient, never()).emailFormatterAndReader(anyString());
        verify(javaMailSender, never()).sendEmail(anyString(), anyString(), anyString(), anyString());
        assertThat(expectedException)
                .isNotNull()
                .isInstanceOf(InvitationSendFailureException.class)
                .hasMessageContaining("Impossible to send invitation - current user or current company is null");
    }

    @Test
    @DisplayName("Should thrown an exception if error occur due getting current user dto")
    void test_11() {
        //when
        when(httpClient.getCurrentUserDto(userEmail)).thenReturn(null);
        when(httpClient.getCompanyDtoById(companyId, userEmail)).thenReturn(sampleCompanyDto);

        final var expectedException = catchThrowable(() -> emailService.sendInvitationEmail(sampleInvitationDto, userEmail));

        //then
        verify(fileReaderClient, never()).emailFormatterAndReader(anyString());
        verify(javaMailSender, never()).sendEmail(anyString(), anyString(), anyString(), anyString());
        assertThat(expectedException)
                .isNotNull()
                .isInstanceOf(InvitationSendFailureException.class)
                .hasMessageContaining("Impossible to send invitation - current user or current company is null");
    }
}