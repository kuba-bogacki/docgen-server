package com.notification.service;

import com.notification.exception.CurrentUserNotFoundException;
import com.notification.mapper.NotificationMapper;
import com.notification.model.dto.UserDto;
import com.notification.repository.NotificationRepository;
import com.notification.service.implementation.NotificationServiceImplementation;
import jakarta.ws.rs.NotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplementationTest extends NotificationSamples {

    @SuppressWarnings("rawtypes")
    @Mock private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
    @SuppressWarnings("rawtypes")
    @Mock private WebClient.RequestHeadersSpec requestHeadersSpec;
    @Mock private WebClient.ResponseSpec responseSpec;
    @Mock private WebClient.Builder webClientBuilder;
    @Mock private WebClient webClient;
    @Mock private NotificationMapper notificationMapper;
    @Mock private SimpMessagingTemplate simpMessagingTemplate;
    @Mock private NotificationRepository notificationRepository;
    @InjectMocks private NotificationServiceImplementation notificationService;

    @Test
    @SuppressWarnings("unchecked")
    @DisplayName("Should return notification dto list if current user id was provided")
    void test_01() {
        //given
        final var sampleJwtToken = "sampleJwtToken";
        final var sampleUri = "http://authentication-service/v1.0/authentication/user";

        //when
        when(webClientBuilder.filter(any())).thenReturn(webClientBuilder);
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(sampleUri)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(UserDto.class)).thenReturn(Mono.just(sampleUserDto));

        when(notificationRepository.findNotificationsByNotificationReceiverId(anyString())).thenReturn(List.of(notificationEntity1, notificationEntity2));
        when(notificationMapper.toNotificationDtoList(List.of(notificationEntity1, notificationEntity2))).thenReturn(List.of(notificationDto1, notificationDto2));

        final var result = notificationService.getUserNotifications(sampleJwtToken);

        //then
        verify(notificationRepository).findNotificationsByNotificationReceiverId(anyString());
        verify(notificationMapper).toNotificationDtoList(List.of(notificationEntity1, notificationEntity2));
        assertThat(result)
                .isNotEmpty()
                .hasSize(2);
    }

    @Test
    @SuppressWarnings("unchecked")
    @DisplayName("Should return empty list if no notification was found for current user")
    void test_02() {
        //given
        final var sampleJwtToken = "sampleJwtToken";
        final var sampleUri = "http://authentication-service/v1.0/authentication/user";

        //when
        when(webClientBuilder.filter(any())).thenReturn(webClientBuilder);
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(sampleUri)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(UserDto.class)).thenReturn(Mono.just(sampleUserDto));

        when(notificationRepository.findNotificationsByNotificationReceiverId(anyString())).thenReturn(Collections.emptyList());
        when(notificationMapper.toNotificationDtoList(Collections.emptyList())).thenReturn(Collections.emptyList());

        final var result = notificationService.getUserNotifications(sampleJwtToken);

        //then
        verify(notificationRepository).findNotificationsByNotificationReceiverId(anyString());
        verify(notificationMapper).toNotificationDtoList(Collections.emptyList());
        assertThat(result)
                .isEmpty();
    }

    @Test
    @SuppressWarnings("unchecked")
    @DisplayName("Should throw an exception if current user id was null")
    void test_03() {
        //given
        final var sampleJwtToken = "sampleJwtToken";
        final var sampleUri = "http://authentication-service/v1.0/authentication/user";
        final var noUuidUserDto = sampleUserDto.toBuilder()
                .userId(null)
                .build();

        //when
        when(webClientBuilder.filter(any())).thenReturn(webClientBuilder);
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(sampleUri)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(UserDto.class)).thenReturn(Mono.just(noUuidUserDto));

        final var expectedException = catchException(() -> notificationService.getUserNotifications(sampleJwtToken));

        //then
        assertThat(expectedException)
                .isNotNull()
                .isInstanceOf(CurrentUserNotFoundException.class)
                .hasMessageContaining("Impossible to get current user id by token credential");
    }
}