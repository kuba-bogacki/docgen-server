package com.notification.service.implementation;

import com.notification.configuration.properties.JwtProperties;
import com.notification.exception.CurrentUserNotFoundException;
import com.notification.infrastructure.HttpClient;
import com.notification.mapper.NotificationMapper;
import com.notification.repository.NotificationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceImplementationTest extends NotificationSamples {

    @Mock private HttpClient httpClient;
    @Mock private JwtProperties jwtProperties;
    @Mock private NotificationMapper notificationMapper;
    @Mock private SimpMessagingTemplate simpMessagingTemplate;
    @Mock private NotificationRepository notificationRepository;
    @InjectMocks private NotificationServiceImplementation notificationService;

    @Test
    @DisplayName("Should return notification dto list if current user id was provided")
    void test_01() {
        //when
        when(httpClient.getCurrentUserDto(userEmail)).thenReturn(sampleUserDto);

        when(notificationRepository.findNotificationsByNotificationReceiverId(anyString())).thenReturn(List.of(notificationEntity1, notificationEntity2));
        when(notificationMapper.toNotificationDtoList(List.of(notificationEntity1, notificationEntity2))).thenReturn(List.of(notificationDto1, notificationDto2));

        final var result = notificationService.getUserNotifications(userEmail);

        //then
        verify(notificationRepository).findNotificationsByNotificationReceiverId(anyString());
        verify(notificationMapper).toNotificationDtoList(List.of(notificationEntity1, notificationEntity2));
        assertThat(result)
                .isNotEmpty()
                .hasSize(2);
    }

    @Test
    @DisplayName("Should return empty list if no notification was found for current user")
    void test_02() {
        //when
        when(httpClient.getCurrentUserDto(userEmail)).thenReturn(sampleUserDto);

        when(notificationRepository.findNotificationsByNotificationReceiverId(anyString())).thenReturn(Collections.emptyList());
        when(notificationMapper.toNotificationDtoList(Collections.emptyList())).thenReturn(Collections.emptyList());

        final var result = notificationService.getUserNotifications(userEmail);

        //then
        verify(notificationRepository).findNotificationsByNotificationReceiverId(anyString());
        verify(notificationMapper).toNotificationDtoList(Collections.emptyList());
        assertThat(result)
                .isEmpty();
    }

    @Test
    @DisplayName("Should throw an exception if current user id was null")
    void test_03() {
        //given
        final var noUuidUserDto = sampleUserDto.toBuilder()
                .userId(null)
                .build();

        //when
        when(httpClient.getCurrentUserDto(userEmail)).thenReturn(noUuidUserDto);

        final var expectedException = catchException(() -> notificationService.getUserNotifications(userEmail));

        //then
        assertThat(expectedException)
                .isNotNull()
                .isInstanceOf(CurrentUserNotFoundException.class)
                .hasMessageContaining("Impossible to get current user id by token credential");
    }

//    @Test
//    @DisplayName("Should send refresh token to user if new generated token and user principal was provided")
//    void test_04() {
//        //given
//        final var refreshToken = "refreshToken";
//
//        //when
//        final var expectedException = catchException(() -> notificationService.sendRefreshToken(refreshToken, userPrincipal));
//
//        //then
//        assertThat(expectedException)
//                .isNull();
//    }

    @Test
    @DisplayName("Should send user principal name if principal number and jwt token was provided")
    void test_05() {
        //given
        final var sampleJwtToken = "sampleJwtToken";

        //when
        when(jwtProperties.getUserEmail(sampleJwtToken)).thenReturn(userEmail);
        when(httpClient.getCurrentUserDto(userEmail)).thenReturn(sampleUserDto);
        when(httpClient.addUserPrincipalName(userPrincipalDto, userEmail)).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        final var expectedException = catchException(() -> notificationService.sendUserPrincipalName(sampleJwtToken, PrincipalTestRecord.of()));

        //then
        assertThat(expectedException)
                .isNull();
    }
}