package com.authentication.controller;

import com.authentication.exception.UserAlreadyExistException;
import com.authentication.exception.UserNotFoundException;
import com.authentication.exception.UserUploadPhotoException;
import com.authentication.model.dto.MembershipDto;
import com.authentication.model.dto.PaymentDto;
import com.authentication.model.dto.UserDto;
import com.authentication.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static com.authentication.util.ApplicationConstants.API_VERSION;
import static com.authentication.util.ApplicationConstants.USER_EMAIL_HEADER;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = API_VERSION + "/authentication")
public class UserController {

    private final UserService userService;

    @GetMapping(value = "/user")
    public ResponseEntity<?> getCurrentUser(@RequestHeader(USER_EMAIL_HEADER) String userEmail) {
        try {
            return new ResponseEntity<>(userService.getUserDtoByUserEmail(userEmail), HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value = "/user/{userEmail}")
    public ResponseEntity<?> getUserDtoByUserEmail(@PathVariable String userEmail) {
        try {
            return new ResponseEntity<>(userService.getUserDtoByUserEmail(userEmail), HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value = "/get-by-id/{userId}")
    public ResponseEntity<?> getUserDtoByUserId(@PathVariable String userId) {
        try {
            return new ResponseEntity<>(userService.getUserDtoByUserId(userId), HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value = "/get-id")
    public ResponseEntity<?> getUserIdFromToken(@RequestHeader(USER_EMAIL_HEADER) String userEmail) {
        try {
            return new ResponseEntity<>(userService.getUserDtoByUserEmail(userEmail).getUserId(), HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping(value = "/user")
    public ResponseEntity<?> updateUserData(@RequestBody UserDto userDto) {
        try {
            return new ResponseEntity<>(userService.updateUserData(userDto), HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping(value = "/photo")
    public ResponseEntity<?> uploadNewUserPhoto(@RequestParam("loadedImage") MultipartFile multipartFile, @RequestHeader(USER_EMAIL_HEADER) String userEmail) {
        try {
            return new ResponseEntity<>(userService.uploadNewUserPhoto(multipartFile, userEmail), HttpStatus.OK);
        } catch (UserNotFoundException | UserUploadPhotoException  e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/company-member/{userEmail}")
    public ResponseEntity<?> getCompanyMember(@PathVariable String userEmail, @RequestBody String companyId) {
        try {
            return new ResponseEntity<>(userService.getUserNotCompanyMember(companyId, userEmail), HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (UserAlreadyExistException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.ALREADY_REPORTED);
        }
    }

    @PostMapping(value = "/create-payment")
    public ResponseEntity<?> createPaymentSession(@RequestBody PaymentDto paymentDto, @RequestHeader(USER_EMAIL_HEADER) String userEmail) {
        return new ResponseEntity<>(userService.createPaymentSession(paymentDto, userEmail), HttpStatus.CREATED);
    }

    @DeleteMapping(value = "/cancel-payment/{paymentIntentId}")
    public ResponseEntity<?> cancelPaymentSession(@PathVariable String paymentIntentId) {
        return new ResponseEntity<>(userService.cancelPaymentSession(paymentIntentId), HttpStatus.ACCEPTED);
    }

    @PutMapping(value = "/update-user-membership")
    public ResponseEntity<?> updateUserMembership(@RequestBody MembershipDto membershipDto, @RequestHeader(USER_EMAIL_HEADER) String userEmail) {
        userService.updateUserMembership(membershipDto.getMembership(), userEmail);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
