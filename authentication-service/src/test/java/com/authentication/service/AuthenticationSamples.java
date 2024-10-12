package com.authentication.service;

import com.authentication.model.User;
import com.authentication.model.dto.UserDto;
import com.authentication.model.dto.UserEventDto;
import com.authentication.model.type.Gender;
import com.authentication.model.type.Role;
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

    UserDto sampleUserDto = UserDto.builder()
            .userId(userId)
            .userFirstNameI(userFirstNameI)
            .userFirstNameII(userFirstNameII)
            .userLastNameI(userLastNameI)
            .userLastNameII(userLastNameII)
            .userEmail(userEmailI)
            .userGender(userGender)
            .userRole(userRole)
            .userVerificationCode(userVerificationCode)
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
}
