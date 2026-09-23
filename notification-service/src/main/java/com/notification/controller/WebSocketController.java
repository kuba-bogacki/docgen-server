package com.notification.controller;

import com.notification.model.dto.NotificationRequest;
import com.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

import static com.notification.util.ApplicationConstants.AUTHORIZATION_HEADER;

@RestController
@RequiredArgsConstructor
public class WebSocketController {

    private final NotificationService notificationService;

    @MessageMapping("/get-principal-name")
    public void sendBackPrincipalNameToUser(@Header(AUTHORIZATION_HEADER) String jwtToken, Principal principal) {
        notificationService.sendUserPrincipalName(jwtToken, principal);
    }

    @MessageMapping("/send-membership-petition")
    public void sendMembershipPetition(@Header(AUTHORIZATION_HEADER) String jwtToken, @Payload NotificationRequest notificationRequest) {
        notificationService.sendMembershipPetition(notificationRequest, jwtToken);
    }

    @MessageMapping("/accept-membership-petition")
    public void acceptMembershipPetition(@Header(AUTHORIZATION_HEADER) String jwtToken, @Payload NotificationRequest notificationRequest) {
        notificationService.acceptMembershipPetition(notificationRequest, jwtToken);
    }

    @MessageMapping("/send-new-event-info")
    public void sendNewEventInfo(@Header(AUTHORIZATION_HEADER) String jwtToken, @Payload NotificationRequest notificationRequest) {
        notificationService.sendNewEventInfo(notificationRequest, jwtToken);
    }

    @MessageMapping("/delete-notification")
    public void deleteUserNotification(@Payload String notificationId) {
        notificationService.deleteUserNotification(notificationId);
    }
}
