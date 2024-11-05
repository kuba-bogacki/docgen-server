package com.authentication.service;

import com.authentication.model.User;
import com.authentication.model.dto.UserDto;
import com.authentication.model.dto.UserEventDto;
import com.authentication.model.dto.UserPrincipalDto;
import com.authentication.model.type.Gender;
import com.authentication.model.type.Role;
import com.authentication.security.AuthenticationRequest;
import com.authentication.security.RegisterRequest;

import java.util.UUID;

class AuthenticationSamples {

    final UUID userIdI = UUID.fromString("aa849e5e-981a-4311-8d4e-7bff7577bbde");
    final UUID userIdII = UUID.fromString("219c1223-05fa-4083-91c3-459934bf37e9");
    final UUID userIdIII = UUID.fromString("3c5699db-aba7-4e26-9126-2c1e9a30fbc6");
    final UUID userIdIV = UUID.fromString("3d7ab3b6-0107-4618-9255-a2324f26aa0d");
    final String userFirstNameI = "John";
    final String userFirstNameII = "Paul";
    final String userLastNameI = "Doe";
    final String userLastNameII = "Carter";
    final String userEmail = "john.doe@gmail.com";
    final String userPassword = "secret-password";
    final String userEncodedPassword = "$2a$10$SnbHHtWU07v8K6k";
    final Gender userGender = Gender.MALE;
    final Role userRole = Role.USER;
    final boolean termsAndCondition = true;
    final String userVerificationCode = "HDoLh86MBCMB4uEc3jWkgHfUTGxhbGDZjkgyS2Dm46tysfylyVYhyxJUEvpKDTQf";
    final String userPhotoFileName = "profile-picture-current-photo-file-name.jpg";
    final String userPrincipal = "cf8579c0-b0f1-4f8a-bbc3-cb5e00f1a929";
    final String companyId = "8189d0bf-88e9-4e17-816f-5ef57c482a9d";

    RegisterRequest registerRequest = RegisterRequest.builder()
            .userFirstNameI(userFirstNameI)
            .userFirstNameII(userFirstNameII)
            .userLastNameI(userLastNameI)
            .userLastNameII(userLastNameII)
            .userEmail(userEmail)
            .userPassword(userPassword)
            .userGender(userGender.name())
            .termsAndCondition(termsAndCondition)
            .build();

    AuthenticationRequest sampleAuthenticationRequest = AuthenticationRequest.builder()
            .userEmail(userEmail)
            .userPassword(userPassword)
            .build();

    User sampleUserEntity = User.builder()
            .userFirstNameI(userFirstNameI)
            .userFirstNameII(userFirstNameII)
            .userLastNameI(userLastNameI)
            .userLastNameII(userLastNameII)
            .userEmail(userEmail)
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

    UserDto sampleUserDto = UserDto.builder()
            .userFirstNameI(userFirstNameI)
            .userFirstNameII(userFirstNameII)
            .userLastNameI(userLastNameI)
            .userLastNameII(userLastNameII)
            .userEmail(userEmail)
            .userGender(userGender)
            .userRole(userRole)
            .userVerificationCode(userVerificationCode)
            .userPrincipal(userPrincipal)
            .accountNonExpired(true)
            .accountNonLocked(true)
            .credentialsNonExpired(true)
            .enabled(false)
            .build();

    UserEventDto sampleUserEventDto = UserEventDto.builder()
            .userFirstNameI(userFirstNameI)
            .userLastNameI(userLastNameI)
            .userEmail(userEmail)
            .userVerificationCode(userVerificationCode)
            .build();

    UserPrincipalDto sampleUserPrincipalDto = UserPrincipalDto.builder()
            .userId(userIdI.toString())
            .userPrincipal(userPrincipal)
            .build();
}
