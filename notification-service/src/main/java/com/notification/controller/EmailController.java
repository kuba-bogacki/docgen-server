package com.notification.controller;

import com.notification.exception.ReadEmailContentException;
import com.notification.model.dto.InvitationDto;
import com.notification.model.dto.UserDto;
import com.notification.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

import static com.notification.util.ApplicationConstants.API_VERSION;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = API_VERSION + "/notification")
public class EmailController {

    private final EmailService emailService;

    @PostMapping(value = "/verification")
    public ResponseEntity<?> sendVerificationEmail(@RequestBody UserDto userDto) {
        try {
            emailService.sendVerificationEmail(userDto);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (MessagingException | IOException | ReadEmailContentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = "/reset")
    public ResponseEntity<?> sendResetPasswordEmail(@RequestBody UserDto userDto) {
        try {
            emailService.sendResetPasswordEmail(userDto);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (MessagingException | IOException | ReadEmailContentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = "/invite")
    public ResponseEntity<?> sendInvitationEmail(@RequestBody InvitationDto invitationDto, @RequestHeader("Authorization") String jwtToken) {
        try {
            emailService.sendInvitationEmail(invitationDto, jwtToken);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (MessagingException | IOException | ReadEmailContentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
