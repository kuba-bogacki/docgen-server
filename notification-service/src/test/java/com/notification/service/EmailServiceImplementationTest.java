package com.notification.service;

import com.notification.config.mail.JavaMailSenderConfiguration;
import com.notification.service.implementation.EmailServiceImplementation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.web.reactive.function.client.WebClient;

class EmailServiceImplementationTest {

    @Captor private ArgumentCaptor<String> urlCaptor;
    @Captor private ArgumentCaptor<String> bodyCaptor;
    @Captor private ArgumentCaptor<String> emailCaptor;
    @SuppressWarnings("rawtypes")
    @Mock private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
    @SuppressWarnings("rawtypes")
    @Mock private WebClient.RequestHeadersSpec requestHeadersSpec;
    @Mock private WebClient.RequestBodyUriSpec requestBodyUriSpec;
    @Mock private WebClient.RequestBodySpec requestBodySpec;
    @Mock private WebClient.ResponseSpec responseSpec;
    @Mock private WebClient.Builder webClientBuilder;
    @Mock private WebClient webClient;
    @Mock private JavaMailSenderConfiguration javaMailSender;
    @InjectMocks private EmailServiceImplementation emailService;

    @Test
    @DisplayName("Should send an email if user dto is provided")
    void test_01() {
        //given
//        final var

        //when
//        final var result =

        //them
    }
}