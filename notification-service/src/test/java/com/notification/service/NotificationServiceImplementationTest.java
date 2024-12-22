package com.notification.service;

import com.notification.mapper.NotificationMapper;
import com.notification.model.dto.UserDto;
import com.notification.repository.NotificationRepository;
import com.notification.service.implementation.NotificationServiceImplementation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
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

        //then
    }
}