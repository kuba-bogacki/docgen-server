package com.notification.service;

import com.notification.model.Notification;
import com.notification.model.dto.*;
import com.notification.model.type.NotificationType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.security.Principal;
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

    final String notificationId1 = "f41c16a3-4a19-4388-aa5e-4ff52c861930";
    final String notificationId2 = "0b8af0cf-58eb-4c8d-86f5-9a94f2941b5e";
    final String notificationCompanyId = "90dac5c7-b206-4394-9dfc-dba8abca27b6";
    final String notificationReceiverId1 = "2ecd2c1b-7e3d-41d6-8975-9ade06ffbf5b";
    final String notificationRequesterId1 = "ab8e77d2-1c43-4e30-a6c8-049eff2281ff";
    final String notificationRequesterId2 = "4952c01e-dece-40b6-976d-f0913573310e";
    final String notificationMessage1 = "Notification Title 1";
    final String notificationMessage2 = "Notification Title 2";
    final NotificationType notificationType = NotificationType.EVENT_REQUEST;

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

    Notification notificationEntity1 = Notification.builder()
            .notificationId(notificationId1)
            .notificationCompanyId(notificationCompanyId)
            .notificationUserPrincipal(userPrincipal)
            .notificationRequesterId(notificationRequesterId1)
            .notificationReceiverId(notificationReceiverId1)
            .notificationMessage(notificationMessage1)
            .notificationType(notificationType)
            .build();

    Notification notificationEntity2 = Notification.builder()
            .notificationId(notificationId2)
            .notificationCompanyId(notificationCompanyId)
            .notificationUserPrincipal(userPrincipal)
            .notificationRequesterId(notificationRequesterId2)
            .notificationReceiverId(notificationReceiverId1)
            .notificationMessage(notificationMessage2)
            .notificationType(notificationType)
            .build();

    NotificationDto notificationDto1 = NotificationDto.builder()
            .notificationId(notificationId1)
            .notificationCompanyId(notificationCompanyId)
            .notificationUserPrincipal(userPrincipal)
            .notificationRequesterId(notificationRequesterId1)
            .notificationReceiverId(notificationReceiverId1)
            .notificationMessage(notificationMessage1)
            .notificationType(notificationType.name())
            .build();

    NotificationDto notificationDto2 = NotificationDto.builder()
            .notificationId(notificationId2)
            .notificationCompanyId(notificationCompanyId)
            .notificationUserPrincipal(userPrincipal)
            .notificationRequesterId(notificationRequesterId2)
            .notificationReceiverId(notificationReceiverId1)
            .notificationMessage(notificationMessage2)
            .notificationType(notificationType.name())
            .build();

    UserPrincipalDto userPrincipalDto = UserPrincipalDto.builder()
            .userId(userId)
            .userPrincipal(userPrincipal)
            .build();

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    static class PrincipalTestRecord implements Principal {

        @Override
        public String getName() {
            return "cf8579c0-b0f1-4f8a-bbc3-cb5e00f1a929";
        }

        public static PrincipalTestRecord of() {
            return new PrincipalTestRecord();
        }
    }
}
