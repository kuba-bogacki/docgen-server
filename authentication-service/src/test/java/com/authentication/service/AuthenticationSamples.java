package com.authentication.service;

import com.authentication.model.User;
import com.authentication.model.dto.UserEventDto;
import com.authentication.model.dto.UserPrincipalDto;
import com.authentication.model.type.Gender;
import com.authentication.model.type.Role;
import com.authentication.security.AuthenticationRequest;
import com.authentication.security.RegisterRequest;

import java.util.UUID;

class AuthenticationSamples {

    final UUID userId = UUID.fromString("aa849e5e-981a-4311-8d4e-7bff7577bbde");
    final String userFirstNameI = "John";
    final String userFirstNameII = "Paul";
    final String userLastNameI = "Doe";
    final String userLastNameII = "Carter";
    final String userEmailI = "john.doe@gmail.com";
    final String userPassword = "secret-password";
    final String userEncodedPassword = "$2a$10$SnbHHtWU07v8K6k";
    final Gender userGender = Gender.MALE;
    final Role userRole = Role.USER;
    final boolean termsAndCondition = true;
    final String userVerificationCode = "HDoLh86MBCMB4uEc3jWkgHfUTGxhbGDZjkgyS2Dm46tysfylyVYhyxJUEvpKDTQf";
    final String userPrincipal = "cf8579c0-b0f1-4f8a-bbc3-cb5e00f1a929";
    final String companyId = "8189d0bf-88e9-4e17-816f-5ef57c482a9d";

    RegisterRequest registerRequest = RegisterRequest.builder()
            .userFirstNameI(userFirstNameI)
            .userFirstNameII(userFirstNameII)
            .userLastNameI(userLastNameI)
            .userLastNameII(userLastNameII)
            .userEmail(userEmailI)
            .userPassword(userPassword)
            .userGender(userGender.name())
            .termsAndCondition(termsAndCondition)
            .build();

    AuthenticationRequest sampleAuthenticationRequest = AuthenticationRequest.builder()
            .userEmail(userEmailI)
            .userPassword(userPassword)
            .build();

    User sampleUserEntity = User.builder()
            .userFirstNameI(userFirstNameI)
            .userFirstNameII(userFirstNameII)
            .userLastNameI(userLastNameI)
            .userLastNameII(userLastNameII)
            .userEmail(userEmailI)
            .userPassword(userEncodedPassword)
            .userGender(userGender)
            .userRole(userRole)
            .userVerificationCode(userVerificationCode)
            .termsAndCondition(termsAndCondition)
            .accountNonExpired(true)
            .accountNonLocked(true)
            .credentialsNonExpired(true)
            .enabled(false)
            .build();

    UserEventDto sampleUserEventDto = UserEventDto.builder()
            .userFirstNameI(userFirstNameI)
            .userLastNameI(userLastNameI)
            .userEmail(userEmailI)
            .userVerificationCode(userVerificationCode)
            .build();

    UserPrincipalDto sampleUserPrincipalDto = UserPrincipalDto.builder()
            .userId(userId.toString())
            .userPrincipal(userPrincipal)
            .build();
}
