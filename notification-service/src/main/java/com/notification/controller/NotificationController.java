package com.notification.controller;

import com.notification.model.dto.NotificationRequest;
import com.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

import static com.notification.util.ApplicationConstants.API_VERSION;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = API_VERSION + "/notification")
public class NotificationController {

    private final NotificationService notificationService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final SimpUserRegistry userRegistry;

    @PutMapping(value = "/set-websocket-handshake/{userId}")
    public ResponseEntity<?> sendMembershipPetition(@PathVariable String userId) {
        try {
            return new ResponseEntity<>(notificationService.putCustomHandshake(userId), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(Boolean.FALSE, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/membership-petition")
    public ResponseEntity<?> sendMembershipPetition(@RequestBody NotificationRequest notificationRequest, @RequestHeader("Authorization") String jwtToken) {
        try {
//            notificationService.sendMembershipPetition(notificationRequest, jwtToken);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @MessageMapping("/broadcast")
    @SendToUser("/topic/replay/{companyId}")
    public void petitionNotificationMessage(@Header("Authorization") String jwtToken, @Payload NotificationRequest notificationRequest, Principal principal) {
        try {
            System.out.println(userRegistry.);
            notificationService.sendMembershipPetition(notificationRequest, jwtToken, principal);
//            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
//            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

//        simpMessagingTemplate.convertAndSendToUser(principal.getName(), "/topic/replay", notificationRequest.getNotificationMessage());
//        return notificationRequest.getNotificationMessage();
    }
}
