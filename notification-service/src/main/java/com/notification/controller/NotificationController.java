package com.notification.controller;

import com.notification.model.dto.AuthenticationResponse;
import com.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.notification.util.ApplicationConstants.API_VERSION;
import static com.notification.util.ApplicationConstants.USER_EMAIL_HEADER;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = API_VERSION + "/notification")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping(value = "/get-user-notifications")
    public ResponseEntity<?> getUserNotification(@RequestHeader(USER_EMAIL_HEADER) String userEmail) {
        try {
            return new ResponseEntity<>(notificationService.getUserNotifications(userEmail), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/refresh-token/{userPrincipal}")
    public ResponseEntity<?> refreshToken(@RequestBody AuthenticationResponse authenticationResponse, @PathVariable String userPrincipal) {
        try {
            notificationService.sendRefreshToken(authenticationResponse, userPrincipal);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception exception) {
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
