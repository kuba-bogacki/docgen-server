package com.authentication.service;

import com.authentication.exception.UserAlreadyExistException;
import com.authentication.exception.UserAuthenticationException;
import com.authentication.exception.UserAuthorizationException;
import com.authentication.exception.UserNotFoundException;
import com.authentication.mapper.UserMapper;
import com.authentication.repository.UserRepository;
import com.authentication.security.AuthenticationResponse;
import com.authentication.service.implementation.AuthenticationServiceImplementation;
import com.authentication.util.NumberGenerator;
import io.jsonwebtoken.JwtException;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
        verify(passwordEncoder, never()).encode(userPassword);
        verify(numberGenerator, never()).generateVerificationCode();
        verify(userMapper, never()).mapToUserEventDto(sampleUserEntity);
        verify(userRepository, never()).save(sampleUserEntity);
    }

    @Test
    @SuppressWarnings("unchecked")
    @DisplayName("Should throw an exception if notification web client response error occur")
    void test_03() {
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
        when(responseSpec.toEntity(ResponseEntity.class)).thenReturn(Mono.just(new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE)));

        final var catchException = catchThrowable(() -> authenticationService.register(registerRequest));

        //then
        assertThat(catchException)
                .isNotNull()
                .isInstanceOf(UserAuthenticationException.class)
                .hasMessageContaining("Couldn't send verification email. New user is not saved in database.");
        verify(passwordEncoder).encode(userPassword);
        verify(numberGenerator).generateVerificationCode();
        verify(userMapper).mapToUserEventDto(sampleUserEntity);
        verify(userRepository, never()).save(sampleUserEntity);
    }

    @Test
    @SuppressWarnings("unchecked")
    @DisplayName("Should throw an exception if notification web client return null")
    void test_04() {
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
        when(responseSpec.toEntity(ResponseEntity.class)).thenReturn(Mono.empty());

        final var catchException = catchThrowable(() -> authenticationService.register(registerRequest));

        //then
        assertThat(catchException)
                .isNotNull()
                .isInstanceOf(UserAuthenticationException.class)
                .hasMessageContaining("Couldn't send verification email. New user is not saved in database.");
        verify(passwordEncoder).encode(userPassword);
        verify(numberGenerator).generateVerificationCode();
        verify(userMapper).mapToUserEventDto(sampleUserEntity);
        verify(userRepository, never()).save(sampleUserEntity);
    }

    @Test
    @SuppressWarnings("unchecked")
    @DisplayName("Should throw an exception if some required non nullable parameter will be null")
    void test_05() {
        //given
        final var sampleUri = "http://notification-service/v1.0/notification/verification";

        //when
        when(userRepository.findUserByUserEmail(userEmailI)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(userPassword)).thenReturn(userEncodedPassword);
        when(numberGenerator.generateVerificationCode()).thenReturn(userVerificationCode);
        when(userMapper.mapToUserEventDto(sampleUserEntity)).thenReturn(sampleUserEventDto);
        doThrow(ConstraintViolationException.class).when(userRepository).save(sampleUserEntity);

        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(sampleUri)).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(sampleUserEventDto)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toEntity(ResponseEntity.class)).thenReturn(Mono.just(new ResponseEntity<>(HttpStatus.OK)));

        final var catchException = catchThrowable(() -> authenticationService.register(registerRequest));

        //then
        assertThat(catchException)
                .isNotNull()
                .isInstanceOf(ConstraintViolationException.class);
        verify(passwordEncoder).encode(userPassword);
        verify(numberGenerator).generateVerificationCode();
        verify(userMapper).mapToUserEventDto(sampleUserEntity);
        verify(userRepository).save(sampleUserEntity);
    }

    @Test
    @DisplayName("Should add user principal number if user entity exist and user principal dto is valid")
    void test_06() {
        //given
        final var userEntity = sampleUserEntity
                .toBuilder()
                .userId(userId)
                .build();
        final var updatedUser = userEntity
                .toBuilder()
                .userPrincipal(userPrincipal)
                .build();

        //when
        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        when(userRepository.save(updatedUser)).thenReturn(updatedUser);

        final var catchException = catchThrowable(() -> authenticationService.addUserPrincipal(sampleUserPrincipalDto));

        //then
        assertThat(catchException)
                .isNull();
        verify(userRepository).findById(userId);
        verify(userRepository).save(updatedUser);
    }

    @Test
    @DisplayName("Should throw an exception if user entity to update not exist")
    void test_07() {
        //when
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        final var catchException = catchThrowable(() -> authenticationService.addUserPrincipal(sampleUserPrincipalDto));

        //then
        assertThat(catchException)
                .isNotNull()
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("Impossible to find user with provide id");
        verify(userRepository).findById(userId);
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should return jwt token if user verification code is same as saved in user entity")
    void test_08() {
        //given
        final var sampleToken = "sample-secret-token";
        final var userEntity = sampleUserEntity
                .toBuilder()
                .userId(userId)
                .build();
        final var updatedEntity = userEntity
                .toBuilder()
                .enabled(true)
                .build();
        final var usernamePasswordAuthentication = new UsernamePasswordAuthenticationToken(
                sampleAuthenticationRequest.getUserEmail(), sampleAuthenticationRequest.getUserPassword());

        //when
        when(userRepository.findUserByUserEmail(userEmailI)).thenReturn(Optional.of(userEntity));
        when(authenticationManager.authenticate(usernamePasswordAuthentication)).thenReturn(any());
        when(userRepository.save(updatedEntity)).thenReturn(updatedEntity);
        when(jwtService.generateJwtToken(updatedEntity)).thenReturn(sampleToken);

        final var result = authenticationService.verifyUserRegistrationCode(userVerificationCode, sampleAuthenticationRequest);

        //then
        assertThat(result)
                .isNotNull()
                .isInstanceOf(AuthenticationResponse.class)
                .hasFieldOrPropertyWithValue("jwtToken", sampleToken);
        verify(userRepository).findUserByUserEmail(userEmailI);
        verify(authenticationManager).authenticate(usernamePasswordAuthentication);
        verify(userRepository).save(updatedEntity);
        verify(jwtService).generateJwtToken(updatedEntity);
    }

    @Test
    @DisplayName("Should throw an exception if user entity not found by provided email")
    void test_09() {
        //when
        when(userRepository.findUserByUserEmail(userEmailI)).thenReturn(Optional.empty());

        final var expectedException = catchThrowable(() -> authenticationService.verifyUserRegistrationCode(userVerificationCode, sampleAuthenticationRequest));

        //then
        assertThat(expectedException)
                .isNotNull()
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("Impossible to find user with provided email");
        verify(userRepository).findUserByUserEmail(userEmailI);
        verify(authenticationManager, never()).authenticate(any());
        verify(userRepository, never()).save(any());
        verify(jwtService, never()).generateJwtToken(any());
    }

    @Test
    @DisplayName("Should throw an exception if user verification code is not same as provided registration code")
    void test_10() {
        //given
        final var differentRegistrationCode = "asdLh86MBCMB4uEc3jWgsgHfUTGxhbGDZjkgyghjk46tysfylyV46yxJUEvpKDTQf";
        final var userEntity = sampleUserEntity
                .toBuilder()
                .userId(userId)
                .build();

        //when
        when(userRepository.findUserByUserEmail(userEmailI)).thenReturn(Optional.of(userEntity));

        final var expectedException = catchThrowable(() -> authenticationService.verifyUserRegistrationCode(differentRegistrationCode, sampleAuthenticationRequest));

        //then
        assertThat(expectedException)
                .isNotNull()
                .isInstanceOf(UserAuthenticationException.class)
                .hasMessageContaining("Verification codes are different or code already expired");
        verify(userRepository).findUserByUserEmail(userEmailI);
        verify(authenticationManager, never()).authenticate(any());
        verify(userRepository, never()).save(any());
        verify(jwtService, never()).generateJwtToken(any());
    }

    @Test
    @DisplayName("Should throw an exception if provided user password is incorrect")
    void test_11() {
        //given
        final var wrongPassword = "wrongPassword";
        final var userEntity = sampleUserEntity
                .toBuilder()
                .userId(userId)
                .build();
        final var wrongCredentialsAuthenticationRequest = sampleAuthenticationRequest.toBuilder()
                .userPassword(wrongPassword)
                .build();
        final var usernamePasswordAuthentication = new UsernamePasswordAuthenticationToken(
                wrongCredentialsAuthenticationRequest.getUserEmail(), wrongCredentialsAuthenticationRequest.getUserPassword());

        //when
        when(userRepository.findUserByUserEmail(userEmailI)).thenReturn(Optional.of(userEntity));
        when(authenticationManager.authenticate(usernamePasswordAuthentication)).thenThrow(UserAuthorizationException.class);

        final var expectedException = catchThrowable(() -> authenticationService.verifyUserRegistrationCode(userVerificationCode, wrongCredentialsAuthenticationRequest));

        //then
        assertThat(expectedException)
                .isNotNull()
                .isInstanceOf(UserAuthorizationException.class)
                .hasMessageContaining("Bad credentials. Impossible to authenticate user with provide email or password");
        verify(userRepository).findUserByUserEmail(userEmailI);
        verify(authenticationManager).authenticate(usernamePasswordAuthentication);
        verify(userRepository, never()).save(any());
        verify(jwtService, never()).generateJwtToken(any());
    }

    @Test
    @DisplayName("Should throw an exception if some issue occur during generate jwt token")
    void test_12() {
        //given
        final var userEntity = sampleUserEntity
                .toBuilder()
                .userId(userId)
                .build();
        final var updatedEntity = userEntity
                .toBuilder()
                .enabled(true)
                .build();
        final var usernamePasswordAuthentication = new UsernamePasswordAuthenticationToken(
                sampleAuthenticationRequest.getUserEmail(), sampleAuthenticationRequest.getUserPassword());

        //when
        when(userRepository.findUserByUserEmail(userEmailI)).thenReturn(Optional.of(userEntity));
        when(authenticationManager.authenticate(usernamePasswordAuthentication)).thenReturn(any());
        when(userRepository.save(updatedEntity)).thenReturn(updatedEntity);
        when(jwtService.generateJwtToken(updatedEntity)).thenThrow(JwtException.class);

        final var expectedException = catchThrowable(() -> authenticationService.verifyUserRegistrationCode(userVerificationCode, sampleAuthenticationRequest));

        //then
        assertThat(expectedException)
                .isNotNull()
                .isInstanceOf(JwtException.class);
        verify(userRepository).findUserByUserEmail(userEmailI);
        verify(authenticationManager).authenticate(usernamePasswordAuthentication);
        verify(userRepository).save(updatedEntity);
        verify(jwtService).generateJwtToken(updatedEntity);
    }
}