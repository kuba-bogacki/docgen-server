package com.authentication.controller;

import com.authentication.exception.*;
import com.authentication.model.dto.UserPrincipalDto;
import com.authentication.security.AuthenticationRequest;
import com.authentication.security.RegisterRequest;
import com.authentication.service.AuthenticationService;
import com.authentication.service.JwtService;
import com.authentication.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.authentication.util.ApplicationConstants.API_VERSION;
import static com.authentication.util.ApplicationConstants.EMAIL_PATTERN;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = API_VERSION + "/authentication")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final JwtService jwtService;
    private final UserService userService;

    @PostMapping(value = "/create")
    public ResponseEntity<?> createNewUser(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            authenticationService.register(registerRequest);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (UserAlreadyExistException | UserAuthenticationException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping(value = "/verify/{registrationCode}")
    public ResponseEntity<?> verifyUser(@PathVariable String registrationCode, @Valid @RequestBody AuthenticationRequest authenticationRequest) {
        try {
            return new ResponseEntity<>(authenticationService.verifyUserRegistrationCode(registrationCode, authenticationRequest), HttpStatus.OK);
        } catch (UserNotFoundException | UserAuthenticationException | UserAuthorizationException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.METHOD_NOT_ALLOWED);
        }
    }

    @PostMapping(value = "/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody AuthenticationRequest authenticationRequest) {
        try {
            return new ResponseEntity<>(authenticationService.authenticate(authenticationRequest), HttpStatus.OK);
        } catch (UserNotFoundException | UserAccountDisableException | UserAuthorizationException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.METHOD_NOT_ALLOWED);
        }
    }

    @PostMapping(value = "/send-email-to-reset-password")
    public ResponseEntity<?> sendEmailWithResetPasswordLink(@Email(regexp = EMAIL_PATTERN) @RequestParam("userEmail") String userEmail) {
        try {
            return new ResponseEntity<>(userService.sendVerificationEmail(userEmail), HttpStatus.OK);
        } catch (UserNotFoundException | UserWebClientException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = "/reset-password/{verificationCode}")
    public ResponseEntity<?> resetCustomerPassword(@PathVariable String verificationCode, @Valid @RequestBody AuthenticationRequest authenticationRequest) {
        try {
            return new ResponseEntity<>(userService.resetUserPassword(verificationCode, authenticationRequest), HttpStatus.OK);
        } catch (UserNotFoundException | UserAuthenticationException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String jwtToken) {
        jwtService.validateToken(jwtToken.substring(7));
        return new ResponseEntity<>("Token is valid", HttpStatus.OK);
    }

    @PostMapping(value = "/confirm-membership/{companyId}")
    public ResponseEntity<?> confirmCompanyMembership(@PathVariable("companyId") String companyId, @Valid @RequestBody AuthenticationRequest authenticationRequest) {
        try {
            return new ResponseEntity<>(authenticationService.confirmCompanyMembership(companyId, authenticationRequest), HttpStatus.OK);
        } catch (UserNotFoundException | UserAuthorizationException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }

    @PutMapping(value = "/add-user-principal")
    public ResponseEntity<?> addUserPrincipal(@Valid @RequestBody UserPrincipalDto userPrincipalDto) {
        try {
            authenticationService.addUserPrincipal(userPrincipalDto);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}