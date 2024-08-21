package com.authentication.service;

import com.authentication.exception.UserAccountDisableException;
import com.authentication.exception.UserAlreadyExistException;
import com.authentication.exception.UserAuthenticationException;
import com.authentication.exception.UserNotFoundException;
import com.authentication.security.AuthenticationRequest;
import com.authentication.security.AuthenticationResponse;
import com.authentication.security.RegisterRequest;
import com.authentication.model.dto.UserPrincipalDto;

public interface AuthenticationService {
    void register(RegisterRequest registerRequest) throws UserAlreadyExistException, UserAuthenticationException;
    void addUserPrincipal(UserPrincipalDto userPrincipalDto) throws UserNotFoundException;
    AuthenticationResponse verifyUserRegistrationCode(String registrationCode, AuthenticationRequest authenticationRequest) throws UserNotFoundException, UserAuthenticationException;
    AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) throws UserNotFoundException, UserAccountDisableException;
    AuthenticationResponse confirmCompanyMembership(String companyId, AuthenticationRequest authenticationRequest) throws UserNotFoundException, UserAccountDisableException;
}
