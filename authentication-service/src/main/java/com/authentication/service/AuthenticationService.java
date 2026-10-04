package com.authentication.service;

import com.authentication.model.dto.UserPrincipalDto;
import com.authentication.security.AuthenticationRequest;
import com.authentication.security.AuthenticationResponse;
import com.authentication.security.RegisterRequest;

public interface AuthenticationService {

    void logout(String userEmail);
    void register(RegisterRequest registerRequest);
    void refreshToken(String userEmail);
    void addUserPrincipal(UserPrincipalDto userPrincipalDto);
    AuthenticationResponse verifyUserRegistrationCode(String registrationCode, AuthenticationRequest authenticationRequest);
    AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest);
    AuthenticationResponse confirmCompanyMembership(String companyId, AuthenticationRequest authenticationRequest);
}
