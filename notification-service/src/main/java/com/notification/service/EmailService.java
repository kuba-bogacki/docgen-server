package com.notification.service;

import com.notification.model.dto.InvitationDto;
import com.notification.model.dto.UserDto;
import jakarta.mail.MessagingException;

import java.io.IOException;

public interface EmailService {
    void sendVerificationEmail(UserDto userDto) throws MessagingException, IOException;
    void sendResetPasswordEmail(UserDto userDto) throws MessagingException, IOException;
    void sendInvitationEmail(InvitationDto invitationDto, String jwtToken) throws MessagingException, IOException;
}
