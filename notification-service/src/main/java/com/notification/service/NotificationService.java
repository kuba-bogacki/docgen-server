package com.notification.service;

import com.notification.model.dto.AuthenticationResponse;
import com.notification.model.dto.NotificationDto;
import com.notification.model.dto.NotificationRequest;

import java.security.Principal;
import java.util.List;

public interface NotificationService {
    List<NotificationDto> getUserNotifications(String userEmail);
    void sendRefreshToken(AuthenticationResponse authenticationResponse, String userPrincipal);
    void sendUserPrincipalName(String jwtToken, Principal principal);
    void deleteUserNotification(String notificationId);
    void sendMembershipPetition(NotificationRequest notificationRequest, String jwtToken);
    void acceptMembershipPetition(NotificationRequest notificationRequest, String jwtToken);
    void sendNewEventInfo(NotificationRequest notificationRequest, String jwtToken);
}
