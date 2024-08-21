package com.notification.controller;

import com.notification.model.dto.NotificationRequest;
import com.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
public class WebSocketController {

    private final NotificationService notificationService;

    @MessageMapping("/get-principal-name")
    public void sendBackPrincipalNameToUser(@Header("Authorization") String jwtToken, Principal principal) {
        notificationService.sendUserPrincipalName(principal, jwtToken);
    }

    @MessageMapping("/send-membership-petition")
    public void sendMembershipPetition(@Header("Authorization") String jwtToken, @Payload NotificationRequest notificationRequest) {
        notificationService.sendMembershipPetition(notificationRequest, jwtToken);
    }

    @MessageMapping("/accept-membership-petition")
    public void acceptMembershipPetition(@Header("Authorization") String jwtToken, @Payload NotificationRequest notificationRequest) {
        notificationService.acceptMembershipPetition(notificationRequest, jwtToken);
    }

    @MessageMapping("/send-new-event-info")
    public void sendNewEventInfo(@Header("Authorization") String jwtToken, @Payload NotificationRequest notificationRequest) {
        notificationService.sendNewEventInfo(notificationRequest, jwtToken);
    }

    @MessageMapping("/delete-notification")
    public void deleteUserNotification(@Payload String notificationId) {
        notificationService.deleteUserNotification(notificationId);
    }
}
