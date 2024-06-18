package com.notification.service;

import com.notification.model.dto.NotificationRequest;

import java.security.Principal;

public interface NotificationService {
    void sendMembershipPetition(NotificationRequest notificationRequest, String jwtToken, Principal principal);
    Boolean putCustomHandshake(String userId);
}
