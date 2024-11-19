package com.notification.service;

import com.notification.model.dto.CompanyDto;
import com.notification.model.dto.InvitationDto;
import com.notification.model.dto.UserDto;

import java.util.UUID;

class NotificationSamples {

    final String userId = "aa849e5e-981a-4311-8d4e-7bff7577bbde";
    final String companyId = "f6ffd91b-e041-4be5-a5ab-c7ab681e2e91";
    final String companyName = "Company sp. z o.o.";
    final String userFirstNameI = "John";
    final String userLastNameI = "Doe";
    final String userEmail = "john.doe@gmail.com";
    final String userVerificationCode = "HDoLh86MBCMB4uEc3jWkgHfUTGxhbGDZjkgyS2Dm46tysfylyVYhyxJUEvpKDTQf";
    final String userPrincipal = "cf8579c0-b0f1-4f8a-bbc3-cb5e00f1a929";

    UserDto sampleUserDto = UserDto.builder()
            .userId(userId)
            .userFirstNameI(userFirstNameI)
            .userLastNameI(userLastNameI)
            .userEmail(userEmail)
            .userVerificationCode(userVerificationCode)
            .userPrincipal(userPrincipal)
            .build();

    InvitationDto sampleInvitationDto = InvitationDto.builder()
            .userEmail(userEmail)
            .companyId(companyId)
            .build();

    CompanyDto sampleCompanyDto = CompanyDto.builder()
            .companyId(UUID.fromString(companyId))
            .companyName(companyName)
            .build();
}
