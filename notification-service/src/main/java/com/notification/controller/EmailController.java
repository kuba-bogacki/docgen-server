package com.notification.controller;

import com.notification.model.dto.InvitationDto;
import com.notification.model.dto.UserDto;
import com.notification.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.notification.util.ApplicationConstants.API_VERSION;
import static com.notification.util.ApplicationConstants.USER_EMAIL_HEADER;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = API_VERSION + "/notification")
public class EmailController {

    private final EmailService emailService;

    @PostMapping(value = "/verification")
    public ResponseEntity<?> sendVerificationEmail(@RequestBody UserDto userDto) {
        emailService.sendVerificationEmail(userDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "/reset")
    public ResponseEntity<?> sendResetPasswordEmail(@RequestBody UserDto userDto) {
        emailService.sendResetPasswordEmail(userDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "/invite")
    public ResponseEntity<?> sendInvitationEmail(@RequestBody InvitationDto invitationDto, @RequestHeader(USER_EMAIL_HEADER) String userEmail) {
        emailService.sendInvitationEmail(invitationDto, userEmail);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
