package com.notification.service;

import com.notification.model.dto.UserDto;

public class NotificationSamples {

    final String userIdI = "aa849e5e-981a-4311-8d4e-7bff7577bbde";
    final String userFirstNameI = "John";
    final String userLastNameI = "Doe";
    final String userEmail = "john.doe@gmail.com";
    final String userVerificationCode = "HDoLh86MBCMB4uEc3jWkgHfUTGxhbGDZjkgyS2Dm46tysfylyVYhyxJUEvpKDTQf";
    final String userPrincipal = "cf8579c0-b0f1-4f8a-bbc3-cb5e00f1a929";

    UserDto sampleUserDto = UserDto.builder()
            .userId(userIdI)
            .userFirstNameI(userFirstNameI)
            .userLastNameI(userLastNameI)
            .userEmail(userEmail)
            .userVerificationCode(userVerificationCode)
            .userPrincipal(userPrincipal)
            .build();
}
