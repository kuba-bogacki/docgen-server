package com.authentication.controller;

import com.authentication.model.dto.UserPrincipalDto;
import com.authentication.security.AuthenticationRequest;
import com.authentication.security.RegisterRequest;
import com.authentication.service.AuthenticationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.authentication.util.ApplicationConstants.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = API_VERSION + "/authentication")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping(value = "/create")
    public ResponseEntity<?> createNewUser(@Valid @RequestBody RegisterRequest registerRequest) {
        authenticationService.register(registerRequest);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping(value = "/verify/{registrationCode}")
    public ResponseEntity<?> verifyUser(@PathVariable String registrationCode, @Valid @RequestBody AuthenticationRequest authenticationRequest) {
        return new ResponseEntity<>(authenticationService.verifyUserRegistrationCode(registrationCode, authenticationRequest), HttpStatus.OK);
    }

    @PostMapping(value = "/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody AuthenticationRequest authenticationRequest) {
        return new ResponseEntity<>(authenticationService.authenticate(authenticationRequest), HttpStatus.OK);
    }

    @PostMapping(value = "/refresh")
    public ResponseEntity<?> refreshToken(@Email(regexp = EMAIL_PATTERN) @RequestHeader(USER_EMAIL_HEADER) String userEmail) {
        authenticationService.refreshToken(userEmail);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "/confirm-membership/{companyId}")
    public ResponseEntity<?> confirmCompanyMembership(@PathVariable String companyId, @Valid @RequestBody AuthenticationRequest authenticationRequest) {
        return new ResponseEntity<>(authenticationService.confirmCompanyMembership(companyId, authenticationRequest), HttpStatus.OK);
    }

    @PutMapping(value = "/add-user-principal")
    public ResponseEntity<?> addUserPrincipal(@Valid @RequestBody UserPrincipalDto userPrincipalDto) {
        authenticationService.addUserPrincipal(userPrincipalDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}