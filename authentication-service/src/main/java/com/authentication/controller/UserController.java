package com.authentication.controller;

import com.authentication.exception.UserAlreadyExistException;
import com.authentication.exception.UserNotFoundException;
import com.authentication.model.dto.UserDto;
import com.authentication.service.JwtService;
import com.authentication.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.rmi.NoSuchObjectException;

import static com.authentication.util.ApplicationConstants.API_VERSION;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = API_VERSION + "/authentication")
public class UserController {

    private final JwtService jwtService;
    private final UserService userService;

    @GetMapping(value = "/user")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String jwtToken) {
        try {
            String userEmail = jwtService.extractUsername(jwtToken.substring(7));
            return new ResponseEntity<>(userService.getUserDtoByUserEmail(userEmail), HttpStatus.OK);
        } catch (NoSuchObjectException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value = "/user/{userEmail}")
    public ResponseEntity<?> getUserDtoByUserEmail(@PathVariable("userEmail") String userEmail) {
        try {
            return new ResponseEntity<>(userService.getUserDtoByUserEmail(userEmail), HttpStatus.OK);
        } catch (NoSuchObjectException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value = "/get-by-id/{userId}")
    public ResponseEntity<?> getUserDtoByUserId(@PathVariable("userId") String userId) {
        try {
            return new ResponseEntity<>(userService.getUserDtoByUserId(userId), HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value = "/get-id")
    public ResponseEntity<?> getUserIdFromToken(@RequestHeader("Authorization") String jwtToken) {
        try {
            String userEmail = jwtService.extractUsername(jwtToken.substring(7));
            return new ResponseEntity<>(userService.getUserDtoByUserEmail(userEmail).getUserId(), HttpStatus.OK);
        } catch (NoSuchObjectException e) {
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
    public ResponseEntity<?> uploadNewUserPhoto(@RequestParam("loadedImage") MultipartFile multipartFile, @RequestHeader("Authorization") String jwtToken) {
        try {
            String userEmail = jwtService.extractUsername(jwtToken.substring(7));
            return new ResponseEntity<>(userService.uploadNewUserPhoto(multipartFile, userEmail), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/company-member/{userEmail}")
    public ResponseEntity<?> getCompanyMember(@PathVariable("userEmail") String userEmail, @RequestHeader("Authorization") String jwtToken,
        @RequestBody String companyId) {
        try {
            return new ResponseEntity<>(userService.getUserNotCompanyMember(companyId, jwtToken, userEmail), HttpStatus.OK);
        } catch (NoSuchObjectException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (UserAlreadyExistException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.ALREADY_REPORTED);
        }
    }
}
