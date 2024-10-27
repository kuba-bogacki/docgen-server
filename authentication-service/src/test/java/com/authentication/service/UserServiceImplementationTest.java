package com.authentication.service;

import com.authentication.config.ImageKitConfiguration;
import com.authentication.exception.UserNotFoundException;
import com.authentication.mapper.UserMapper;
import com.authentication.model.User;
import com.authentication.model.dto.UserDto;
import com.authentication.repository.UserRepository;
import com.authentication.service.implementation.UserServiceImplementation;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Optional;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplementationTest extends AuthenticationSamples {

    @SuppressWarnings("rawtypes")
    @Mock private WebClient.RequestHeadersSpec requestHeadersSpec;
    @Mock private WebClient.RequestBodyUriSpec requestBodyUriSpec;
    @Mock private WebClient.RequestBodySpec requestBodySpec;
    @Mock private WebClient.ResponseSpec responseSpec;
    @Mock private WebClient.Builder webClientBuilder;
    @Mock private WebClient webClient;
    @Mock private UserMapper userMapper;
    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private ImageKitConfiguration imageKitConfiguration;
    @InjectMocks private UserServiceImplementation userService;

    @Test
    @DisplayName("Should return user dto if user entity was found by user email")
    void test_01() {
        //given
        final var userDto = sampleUserDto
                .toBuilder()
                .userId(userId)
                .build();
        final var userEntity = sampleUserEntity
                .toBuilder()
                .userId(userId)
                .build();

        //when
        when(userRepository.findUserByUserEmail(userEmail)).thenReturn(Optional.of(userEntity));
        when(userMapper.mapToUserDto(userEntity)).thenReturn(userDto);

        final var result = userService.getUserDtoByUserEmail(userEmail);

        //then
        Assertions.assertThat(result)
                .isNotNull()
                .isInstanceOf(UserDto.class)
                .isEqualTo(userDto);
        verify(userRepository).findUserByUserEmail(userEmail);
        verify(userMapper).mapToUserDto(userEntity);
    }

    @Test
    @DisplayName("Should throw an exception if user entity wasn't found by user email")
    void test_02() {
        //when
        when(userRepository.findUserByUserEmail(userEmail)).thenReturn(Optional.empty());

        final var expectedException = catchThrowable(() -> userService.getUserDtoByUserEmail(userEmail));

        //then
        Assertions.assertThat(expectedException)
                .isNotNull()
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining(String.format("Can't find %s user", userEmail));
        verify(userRepository).findUserByUserEmail(userEmail);
        verify(userMapper, never()).mapToUserDto(any());
    }

    @SuppressWarnings("unchecked")
    @Test
    @DisplayName("Should return true if user entity was found by user email and verification email send successfully")
    void test_03() {
        //given
        final var userEntity = sampleUserEntity
                .toBuilder()
                .userId(userId)
                .build();
        final var sampleUri = "http://notification-service/v1.0/notification/reset";

        //when
        when(userRepository.findUserByUserEmail(userEmail)).thenReturn(Optional.of(userEntity));
        when(userMapper.mapToUserEventDto(userEntity)).thenReturn(sampleUserEventDto);
        when(userRepository.save(userEntity)).thenReturn(userEntity);

        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(sampleUri)).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(sampleUserEventDto)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toEntity(ResponseEntity.class)).thenReturn(Mono.just(new ResponseEntity<>(HttpStatus.OK)));

        final var result = userService.sendVerificationEmail(userEmail);

        //then
        Assertions.assertThat(result)
                .isNotNull()
                .isInstanceOf(Boolean.class)
                .isFalse();
        verify(userRepository).findUserByUserEmail(userEmail);
        verify(userRepository).save(userEntity);
        verify(userMapper).mapToUserEventDto(userEntity);
    }
}