package com.notification.service;

import com.notification.model.dto.NotificationDto;
import com.notification.model.dto.NotificationRequest;

import java.security.Principal;
import java.util.List;

public interface NotificationService {
    List<NotificationDto> getUserNotifications(String jwtToken);
    void sendRefreshToken(String refreshToken, String userPrincipal);
    void sendUserPrincipalName(Principal principal, String jwtToken);
    void deleteUserNotification(String notificationId);
    void sendMembershipPetition(NotificationRequest notificationRequest, String jwtToken);
    void acceptMembershipPetition(NotificationRequest notificationRequest, String jwtToken);
    void sendNewEventInfo(NotificationRequest notificationRequest, String jwtToken);
}
