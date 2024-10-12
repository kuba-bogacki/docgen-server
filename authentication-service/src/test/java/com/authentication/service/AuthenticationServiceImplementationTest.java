package com.authentication.service;

import com.authentication.exception.UserAlreadyExistException;
import com.authentication.mapper.UserMapper;
import com.authentication.repository.UserRepository;
import com.authentication.service.implementation.AuthenticationServiceImplementation;
import com.authentication.util.NumberGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceImplementationTest extends AuthenticationSamples{

    @SuppressWarnings("rawtypes")
    @Mock private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
    @SuppressWarnings("rawtypes")
    @Mock private WebClient.RequestHeadersSpec requestHeadersSpec;
    @Mock private WebClient.RequestBodyUriSpec requestBodyUriSpec;
    @Mock private WebClient.RequestBodySpec requestBodySpec;
    @Mock private WebClient.ResponseSpec responseSpec;
    @Mock private WebClient.Builder webClientBuilder;
    @Mock private WebClient webClient;
    @Mock private JwtService jwtService;
    @Mock private UserMapper userMapper;
    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private NumberGenerator numberGenerator;
    @Mock private AuthenticationManager authenticationManager;
    @InjectMocks private AuthenticationServiceImplementation authenticationService;

    @Test
    @SuppressWarnings("unchecked")
    @DisplayName("Should create new user entity if valid register request is valid")
    void test_01() {
        //given
        final var sampleUri = "http://notification-service/v1.0/notification/verification";

        //when
        when(userRepository.findUserByUserEmail(userEmailI)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(userPassword)).thenReturn(userEncodedPassword);
        when(numberGenerator.generateVerificationCode()).thenReturn(userVerificationCode);
        when(userMapper.mapToUserEventDto(sampleUserEntity)).thenReturn(sampleUserEventDto);

        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(sampleUri)).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(sampleUserEventDto)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toEntity(ResponseEntity.class)).thenReturn(Mono.just(new ResponseEntity<>(HttpStatus.OK)));

        when(userRepository.save(sampleUserEntity)).thenReturn(any());

        final var catchException = catchThrowable(() -> authenticationService.register(registerRequest));

        //then
        assertThat(catchException)
                .isNull();
        verify(userRepository).findUserByUserEmail(userEmailI);
        verify(passwordEncoder).encode(userPassword);
        verify(userMapper).mapToUserEventDto(sampleUserEntity);
        verify(userRepository).save(sampleUserEntity);
    }

    @Test
    @DisplayName("Should throw an exception if try to register second time already existing user")
    void test_02() {
        //given
        final var userEntity = sampleUserEntity
                .toBuilder()
                .userId(userId)
                .build();

        //when
        when(userRepository.findUserByUserEmail(userEmailI)).thenReturn(Optional.of(userEntity));

        final var catchException = catchThrowable(() -> authenticationService.register(registerRequest));

        //then
        assertThat(catchException)
                .isNotNull()
                .isInstanceOf(UserAlreadyExistException.class)
                .hasMessageContaining(String.format("User %s already exist in database.", userEmailI));
    }
}