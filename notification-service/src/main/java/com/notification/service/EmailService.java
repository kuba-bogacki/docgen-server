package com.notification.service;

import com.notification.model.dto.InvitationDto;
import com.notification.model.dto.UserDto;

public interface EmailService {
    void sendVerificationEmail(UserDto userDto);
    void sendResetPasswordEmail(UserDto userDto);
    void sendInvitationEmail(InvitationDto invitationDto, String jwtToken);
}
