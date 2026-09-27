package com.authentication.controller;

import com.authentication.model.dto.MembershipDto;
import com.authentication.model.dto.PaymentDto;
import com.authentication.model.dto.PaymentIntentDto;
import com.authentication.model.dto.UserDto;
import com.authentication.security.AuthenticationRequest;
import com.authentication.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

import static com.authentication.util.ApplicationConstants.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = API_VERSION + "/authentication")
public class UserController {

    private final UserService userService;

    @GetMapping(value = "/user")
    public ResponseEntity<UserDto> getCurrentUser(@Email(regexp = EMAIL_PATTERN) @RequestHeader(USER_EMAIL_HEADER) String userEmail) {
        return new ResponseEntity<>(userService.getUserDtoByUserEmail(userEmail), HttpStatus.OK);
    }

    @GetMapping(value = "/user/{userEmail}")
    public ResponseEntity<UserDto> getUserDtoByUserEmail(@PathVariable String userEmail) {
        return new ResponseEntity<>(userService.getUserDtoByUserEmail(userEmail), HttpStatus.OK);
    }

    @GetMapping(value = "/get-by-id/{userId}")
    public ResponseEntity<UserDto> getUserDtoByUserId(@PathVariable String userId) {
        return new ResponseEntity<>(userService.getUserDtoByUserId(userId), HttpStatus.OK);
    }

    @GetMapping(value = "/get-id")
    public ResponseEntity<UUID> getUserIdFromToken(@Email(regexp = EMAIL_PATTERN) @RequestHeader(USER_EMAIL_HEADER) String userEmail) {
        return new ResponseEntity<>(userService.getUserDtoByUserEmail(userEmail).getUserId(), HttpStatus.OK);
    }

    @PutMapping(value = "/user")
    public ResponseEntity<UserDto> updateUserData(@RequestBody UserDto userDto) {
        return new ResponseEntity<>(userService.updateUserData(userDto), HttpStatus.OK);
    }

    @PostMapping(value = "/photo")
    public ResponseEntity<String> uploadNewUserPhoto(@RequestParam(LOADED_IMAGE_PARAM) MultipartFile multipartFile, @Email(regexp = EMAIL_PATTERN) @RequestHeader(USER_EMAIL_HEADER) String userEmail) {
        return new ResponseEntity<>(userService.uploadNewUserPhoto(multipartFile, userEmail), HttpStatus.OK);
    }

    @PostMapping(value = "/company-member/{userEmail}")
    public ResponseEntity<UserDto> getCompanyMember(@PathVariable String userEmail, @RequestBody String companyId) {
        return new ResponseEntity<>(userService.getUserNotCompanyMember(companyId, userEmail), HttpStatus.OK);
    }

    @PostMapping(value = "/create-payment")
    public ResponseEntity<PaymentIntentDto> createPaymentSession(@Valid @RequestBody PaymentDto paymentDto, @RequestHeader(USER_EMAIL_HEADER) String userEmail) {
        return new ResponseEntity<>(userService.createPaymentSession(paymentDto, userEmail), HttpStatus.CREATED);
    }

    @DeleteMapping(value = "/cancel-payment/{paymentIntentId}")
    public ResponseEntity<String> cancelPaymentSession(@PathVariable String paymentIntentId) {
        return new ResponseEntity<>(userService.cancelPaymentSession(paymentIntentId), HttpStatus.ACCEPTED);
    }

    @PutMapping(value = "/update-user-membership")
    public ResponseEntity<Void> updateUserMembership(@Valid @RequestBody MembershipDto membershipDto, @RequestHeader(USER_EMAIL_HEADER) String userEmail) {
        userService.updateUserMembership(membershipDto.getMembership(), userEmail);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "/send-email-to-reset-password")
    public ResponseEntity<Boolean> sendEmailWithResetPasswordLink(@Email(regexp = EMAIL_PATTERN) @RequestParam(USER_EMAIL_PARAM) String userEmail) {
        return new ResponseEntity<>(userService.sendVerificationEmail(userEmail), HttpStatus.OK);
    }

    @PostMapping(value = "/reset-password/{verificationCode}")
    public ResponseEntity<Boolean> resetCustomerPassword(@PathVariable String verificationCode, @Valid @RequestBody AuthenticationRequest authenticationRequest) {
        return new ResponseEntity<>(userService.resetUserPassword(verificationCode, authenticationRequest), HttpStatus.OK);
    }
}
