package com.notification.controller;

import com.notification.model.dto.InvitationDto;
import com.notification.model.dto.UserDto;
import com.notification.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.notification.util.ApplicationConstants.API_VERSION;

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
    public ResponseEntity<?> sendInvitationEmail(@RequestBody InvitationDto invitationDto, @RequestHeader("Authorization") String jwtToken) {
        emailService.sendInvitationEmail(invitationDto, jwtToken);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
