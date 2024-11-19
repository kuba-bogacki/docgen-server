package com.authentication.service;

import com.authentication.config.imagekit.DefaultImageKitConfiguration;
import com.authentication.exception.*;
import com.authentication.mapper.UserMapper;
import com.authentication.model.dto.UserDto;
import com.authentication.repository.UserRepository;
import com.authentication.service.implementation.UserServiceImplementation;
import com.authentication.util.NumberGenerator;
import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplementationTest extends AuthenticationSamples {

    @SuppressWarnings("rawtypes")
    @Mock private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
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
    @Mock private NumberGenerator numberGenerator;
    @Mock private DefaultImageKitConfiguration imageKitConfiguration;
    @InjectMocks private UserServiceImplementation userService;

    @Test
    @DisplayName("Should return user dto if user entity was found by user email")
    void test_01() {
        //given
        final var userDto = sampleUserDto
                .toBuilder()
                .userId(userIdI)
                .build();
        final var userEntity = sampleUserEntity
                .toBuilder()
                .userId(userIdI)
                .build();

        //when
        when(userRepository.findUserByUserEmail(userEmail)).thenReturn(Optional.of(userEntity));
        when(userMapper.mapToUserDto(userEntity)).thenReturn(userDto);

        final var result = userService.getUserDtoByUserEmail(userEmail);

        //then
        assertThat(result)
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
        assertThat(expectedException)
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
                .userId(userIdI)
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
        assertThat(result)
                .isNotNull()
                .isInstanceOf(Boolean.class)
                .isFalse();
        verify(userRepository).findUserByUserEmail(userEmail);
        verify(userRepository).save(userEntity);
        verify(userMapper).mapToUserEventDto(userEntity);
    }

    @Test
    @DisplayName("Should throw an exception if user entity was not found by user email")
    void test_04() {
        //when
        when(userRepository.findUserByUserEmail(userEmail)).thenReturn(Optional.empty());

        final var expectedException = catchThrowable(() -> userService.sendVerificationEmail(userEmail));

        //then
        assertThat(expectedException)
                .isNotNull()
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("Impossible to send reset link because user with provide email not exist.");
        verify(userRepository).findUserByUserEmail(userEmail);
        verify(userRepository, never()).save(any());
        verify(userMapper, never()).mapToUserEventDto(any());
    }

    @SuppressWarnings("unchecked")
    @Test
    @DisplayName("Should throw an exception if user entity was found by user email but sending verification email was failed")
    void test_05() {
        //given
        final var userEntity = sampleUserEntity
                .toBuilder()
                .userId(userIdI)
                .build();
        final var sampleUri = "http://notification-service/v1.0/notification/reset";

        //when
        when(userRepository.findUserByUserEmail(userEmail)).thenReturn(Optional.of(userEntity));
        when(userMapper.mapToUserEventDto(userEntity)).thenReturn(sampleUserEventDto);

        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(sampleUri)).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(sampleUserEventDto)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toEntity(ResponseEntity.class)).thenReturn(Mono.just(new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE)));

        final var expectedException = catchThrowable(() -> userService.sendVerificationEmail(userEmail));

        //then
        assertThat(expectedException)
                .isNotNull()
                .isInstanceOf(UserWebClientException.class)
                .hasMessageContaining("Couldn't send reset email. User account still locked.");
        verify(userRepository).findUserByUserEmail(userEmail);
        verify(userRepository, never()).save(any());
        verify(userMapper).mapToUserEventDto(userEntity);
    }

    @Test
    @DisplayName("Should reset user password and return true if verification code is provided and authentication request is valid")
    void test_06() {
        //given
        final String newGeneratedVerificationCode = "gDoLh86MfCMB4uEc3jWkgHfUhGTjbGDZjKgyS2Dm46tysUyKyVYRyxJUEvpKDTQf";
        final var userEntity = sampleUserEntity
                .toBuilder()
                .userId(userIdI)
                .build();
        final var entityToSave = userEntity.toBuilder()
                .userVerificationCode(newGeneratedVerificationCode)
                .userPassword(userEncodedPassword)
                .accountNonLocked(true)
                .build();

        //when
        when(userRepository.findUserByUserEmail(sampleAuthenticationRequest.getUserEmail())).thenReturn(Optional.of(userEntity));
        when(numberGenerator.generateVerificationCode(64)).thenReturn(newGeneratedVerificationCode);
        when(passwordEncoder.encode(sampleAuthenticationRequest.getUserPassword())).thenReturn(userEncodedPassword);
        when(userRepository.save(entityToSave)).thenReturn(entityToSave);

        final var result = userService.resetUserPassword(userVerificationCode, sampleAuthenticationRequest);

        //then
        assertThat(result)
                .isNotNull()
                .isInstanceOf(Boolean.class)
                .isTrue();
        verify(userRepository).findUserByUserEmail(sampleAuthenticationRequest.getUserEmail());
        verify(numberGenerator).generateVerificationCode(64);
        verify(passwordEncoder).encode(sampleAuthenticationRequest.getUserPassword());
        verify(userRepository).save(entityToSave);
    }

    @Test
    @DisplayName("Should throw an exception if user entity was not found by user email")
    void test_07() {
        //when
        when(userRepository.findUserByUserEmail(sampleAuthenticationRequest.getUserEmail())).thenReturn(Optional.empty());

        final var expectedException = catchThrowable(() -> userService.resetUserPassword(userVerificationCode, sampleAuthenticationRequest));

        //then
        assertThat(expectedException)
                .isNotNull()
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("Bad credentials. Impossible to find user with provide email.");
        verify(userRepository).findUserByUserEmail(sampleAuthenticationRequest.getUserEmail());
        verify(numberGenerator, never()).generateVerificationCode(64);
        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw an exception if user entity was found by user email authentication request but verification code is different")
    void test_08() {
        //given
        final String differentVerificationCode = "gDoLh86MfCMB4uEc3jWkgHfUhGTjbGDZjKgyS2Dm46tysUyKyVYRyxJUEvpKDTQf";
        final var userEntity = sampleUserEntity
                .toBuilder()
                .userId(userIdI)
                .build();

        //when
        when(userRepository.findUserByUserEmail(sampleAuthenticationRequest.getUserEmail())).thenReturn(Optional.of(userEntity));

        final var expectedException = catchThrowable(() -> userService.resetUserPassword(differentVerificationCode, sampleAuthenticationRequest));

        //then
        assertThat(expectedException)
                .isNotNull()
                .isInstanceOf(UserAuthenticationException.class)
                .hasMessageContaining("Verification codes are different or code already expired.");
        verify(userRepository).findUserByUserEmail(sampleAuthenticationRequest.getUserEmail());
        verify(numberGenerator, never()).generateVerificationCode(64);
        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should return mapped user dto of saved entity if user dto is valid")
    void test_09() {
        //given
        final var updatedUserDto = sampleUserDto.toBuilder()
                .userId(userIdI)
                .userFirstNameI("Michael")
                .userFirstNameII("Anthony")
                .userLastNameI("Corleone")
                .userLastNameII("Hamilton")
                .build();
        final var updatedUserEntity = sampleUserEntity.toBuilder()
                .userId(userIdI)
                .userFirstNameI("Michael")
                .userFirstNameII("Anthony")
                .userLastNameI("Corleone")
                .userLastNameII("Hamilton")
                .build();
        final var foundedEntity = sampleUserEntity.toBuilder()
                .userId(userIdI)
                .build();

        //when
        when(userRepository.findUserByUserEmail(userEmail)).thenReturn(Optional.of(foundedEntity));
        when(userRepository.save(updatedUserEntity)).thenReturn(updatedUserEntity);
        when(userMapper.mapToUserDto(updatedUserEntity)).thenReturn(updatedUserDto);

        final var result = userService.updateUserData(updatedUserDto);

        //then
        assertThat(result)
                .isNotNull()
                .isInstanceOf(UserDto.class)
                .hasFieldOrPropertyWithValue("userFirstNameI", "Michael")
                .hasFieldOrPropertyWithValue("userFirstNameII", "Anthony")
                .hasFieldOrPropertyWithValue("userLastNameI", "Corleone")
                .hasFieldOrPropertyWithValue("userLastNameII", "Hamilton");
        verify(userRepository).findUserByUserEmail(userEmail);
        verify(userRepository).save(updatedUserEntity);
        verify(userMapper).mapToUserDto(updatedUserEntity);
    }

    @Test
    @DisplayName("Should throw an exception if user entity not found by provided user email")
    void test_10() {
        //when
        when(userRepository.findUserByUserEmail(userEmail)).thenReturn(Optional.empty());

        final var expectedException = catchThrowable(() -> userService.updateUserData(sampleUserDto));

        //then
        assertThat(expectedException)
                .isNotNull()
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining(String.format("Can't find %s user", userEmail));
        verify(userRepository).findUserByUserEmail(userEmail);
        verify(userRepository, never()).save(any());
        verify(userMapper, never()).mapToUserDto(any());
    }

    @Test
    @SneakyThrows
    @DisplayName("Should return user photo file name if user entity was found, new photo was updated and result file list is empty")
    void test_11() {
        //given
        final var sampleMultipartFile = new MockMultipartFile("Sample multipart file name", new byte[] {});
        final var sampleFileName = "sample-new-file-name";
        final var updatedPhotoFileName = String.format("profile-picture-%s.jpg", sampleFileName);
        final var userEntity = sampleUserEntity.toBuilder()
                .userId(userIdI)
                .userPhotoFileName(userPhotoFileName)
                .build();

        //when
        when(userRepository.findUserByUserEmail(userEmail)).thenReturn(Optional.of(userEntity));
        when(numberGenerator.generateUserPhotoFileName(26)).thenReturn(sampleFileName);
        when(imageKitConfiguration.uploadImage(sampleMultipartFile.getBytes(), updatedPhotoFileName)).thenReturn(updatedPhotoFileName);
        when(imageKitConfiguration.resultFileListIsEmpty(userPhotoFileName)).thenReturn(true);
        when(userRepository.save(userEntity)).thenReturn(userEntity);

        final var result = userService.uploadNewUserPhoto(sampleMultipartFile, userEmail);

        //then
        assertThat(result)
                .isNotNull()
                .isInstanceOf(String.class)
                .isEqualTo(updatedPhotoFileName);
        verify(userRepository).findUserByUserEmail(userEmail);
        verify(numberGenerator).generateUserPhotoFileName(26);
        verify(imageKitConfiguration).uploadImage(sampleMultipartFile.getBytes(), updatedPhotoFileName);
        verify(imageKitConfiguration).resultFileListIsEmpty(userPhotoFileName);
        verify(userRepository).save(userEntity);
        verify(imageKitConfiguration, never()).deleteFile(anyString());
    }

    @Test
    @SneakyThrows
    @DisplayName("Should return user photo file name if user entity was found, new photo was updated and result file list is not empty")
    void test_12() {
        //given
        final var sampleMultipartFile = new MockMultipartFile("Sample multipart file name", new byte[] {});
        final var sampleFileName = "sample-new-file-name";
        final var updatedPhotoFileName = String.format("profile-picture-%s.jpg", sampleFileName);
        final var userEntity = sampleUserEntity.toBuilder()
                .userId(userIdI)
                .userPhotoFileName(userPhotoFileName)
                .build();

        //when
        when(userRepository.findUserByUserEmail(userEmail)).thenReturn(Optional.of(userEntity));
        when(numberGenerator.generateUserPhotoFileName(26)).thenReturn(sampleFileName);
        when(imageKitConfiguration.uploadImage(sampleMultipartFile.getBytes(), updatedPhotoFileName)).thenReturn(updatedPhotoFileName);
        when(imageKitConfiguration.resultFileListIsEmpty(userPhotoFileName)).thenReturn(false);
        doNothing().when(imageKitConfiguration).deleteFile(userPhotoFileName);
        when(userRepository.save(userEntity)).thenReturn(userEntity);

        final var result = userService.uploadNewUserPhoto(sampleMultipartFile, userEmail);

        //then
        assertThat(result)
                .isNotNull()
                .isInstanceOf(String.class)
                .isEqualTo(updatedPhotoFileName);
        verify(userRepository).findUserByUserEmail(userEmail);
        verify(numberGenerator).generateUserPhotoFileName(26);
        verify(imageKitConfiguration).uploadImage(sampleMultipartFile.getBytes(), updatedPhotoFileName);
        verify(imageKitConfiguration).resultFileListIsEmpty(userPhotoFileName);
        verify(imageKitConfiguration).deleteFile(userPhotoFileName);
        verify(userRepository).save(userEntity);
    }

    @Test
    @SneakyThrows
    @DisplayName("Should throw an exception if user entity not found by provided user email")
    void test_13() {
        //given
        final var sampleMultipartFile = new MockMultipartFile("Sample multipart file name", new byte[] {});

        //when
        when(userRepository.findUserByUserEmail(userEmail)).thenReturn(Optional.empty());

        final var expectedException = catchThrowable(() -> userService.uploadNewUserPhoto(sampleMultipartFile, userEmail));

        //then
        assertThat(expectedException)
                .isNotNull()
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining(String.format("Can't find %s user", userEmail));
        verify(userRepository).findUserByUserEmail(userEmail);
        verify(numberGenerator, never()).generateUserPhotoFileName(anyInt());
        verify(imageKitConfiguration, never()).uploadImage(any(), anyString());
        verify(imageKitConfiguration, never()).resultFileListIsEmpty(anyString());
        verify(imageKitConfiguration, never()).deleteFile(anyString());
        verify(userRepository, never()).save(any());
    }

    @Test
    @SneakyThrows
    @DisplayName("Should throw an exception if user entity was found, but updated new photo failed")
    void test_14() {
        //given
        final var sampleMultipartFile = new MockMultipartFile("Sample multipart file name", new byte[] {});
        final var sampleFileName = "sample-new-file-name";
        final var updatedPhotoFileName = String.format("profile-picture-%s.jpg", sampleFileName);
        final var userEntity = sampleUserEntity.toBuilder()
                .userId(userIdI)
                .build();

        //when
        when(userRepository.findUserByUserEmail(userEmail)).thenReturn(Optional.of(userEntity));
        when(numberGenerator.generateUserPhotoFileName(26)).thenReturn(sampleFileName);
        when(imageKitConfiguration.uploadImage(sampleMultipartFile.getBytes(), updatedPhotoFileName)).thenThrow(IOException.class);

        final var expectedException = catchThrowable(() -> userService.uploadNewUserPhoto(sampleMultipartFile, userEmail));

        //then
        assertThat(expectedException)
                .isNotNull()
                .isInstanceOf(UserUploadPhotoException.class)
                .hasMessageContaining("Couldn't upload result file");
        verify(userRepository).findUserByUserEmail(userEmail);
        verify(numberGenerator).generateUserPhotoFileName(26);
        verify(imageKitConfiguration).uploadImage(sampleMultipartFile.getBytes(), updatedPhotoFileName);
        verify(imageKitConfiguration, never()).resultFileListIsEmpty(anyString());
        verify(imageKitConfiguration, never()).deleteFile(anyString());
        verify(userRepository, never()).save(any());
    }

    @Test
    @SuppressWarnings("unchecked")
    @SneakyThrows
    @DisplayName("Should return not company member user dto if user entity was found and company id was provided")
    void test_15() {
        //given
        final var sampleJwtToken = "sample-jwt-token";
        final var sampleCompanyId = "sample-company-idx";
        final var sampleFormattedCompanyId = "sample-company-id";
        final var sampleUri = String.format("http://company-service/v1.0/company/company-members/%s", sampleFormattedCompanyId);
        final var userEntity = sampleUserEntity.toBuilder()
                .userId(userIdI)
                .build();
        final var userDto = sampleUserDto.toBuilder()
                .userId(userIdI)
                .build();

        //when
        when(userRepository.findUserByUserEmail(userEmail)).thenReturn(Optional.of(userEntity));
        when(userMapper.mapToUserDto(userEntity)).thenReturn(userDto);

        when(webClientBuilder.filter(any())).thenReturn(webClientBuilder);
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(sampleUri)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToFlux(UUID.class)).thenReturn(Flux.fromIterable(List.of(userIdII, userIdIII, userIdIV)));

        final var result = userService.getUserNotCompanyMember(sampleCompanyId, sampleJwtToken, userEmail);

        //then
        assertThat(result)
                .isNotNull()
                .isInstanceOf(UserDto.class);
        verify(userRepository).findUserByUserEmail(userEmail);
        verify(userMapper).mapToUserDto(userEntity);
    }

    @Test
    @DisplayName("Should throw an exception if user entity was not found by provided user email")
    void test_16() {
        //given
        final var sampleJwtToken = "sample-jwt-token";
        final var sampleCompanyId = "sample-company-idx";

        //when
        when(userRepository.findUserByUserEmail(userEmail)).thenReturn(Optional.empty());

        final var expectedException = catchThrowable(() -> userService.getUserNotCompanyMember(sampleCompanyId, sampleJwtToken, userEmail));

        //then
        assertThat(expectedException)
                .isNotNull()
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining(String.format("Can't find %s user", userEmail));
        verify(userRepository).findUserByUserEmail(userEmail);
        verify(userMapper, never()).mapToUserDto(any());
    }

    @Test
    @SuppressWarnings("unchecked")
    @SneakyThrows
    @DisplayName("Should throw an exception if members list contain current user id")
    void test_17() {
        //given
        final var sampleJwtToken = "sample-jwt-token";
        final var sampleCompanyId = "sample-company-idx";
        final var sampleFormattedCompanyId = "sample-company-id";
        final var sampleUri = String.format("http://company-service/v1.0/company/company-members/%s", sampleFormattedCompanyId);
        final var userEntity = sampleUserEntity.toBuilder()
                .userId(userIdI)
                .build();

        //when
        when(userRepository.findUserByUserEmail(userEmail)).thenReturn(Optional.of(userEntity));

        when(webClientBuilder.filter(any())).thenReturn(webClientBuilder);
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(sampleUri)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToFlux(UUID.class)).thenReturn(Flux.fromIterable(List.of(userIdI, userIdII, userIdIII, userIdIV)));

        final var expectedException = catchThrowable(() -> userService.getUserNotCompanyMember(sampleCompanyId, sampleJwtToken, userEmail));

        //then
        assertThat(expectedException)
                .isNotNull()
                .isInstanceOf(UserAlreadyExistException.class)
                .hasMessageContaining(String.format("User with email %s is already member of company", userEmail));
        verify(userRepository).findUserByUserEmail(userEmail);
        verify(userMapper, never()).mapToUserDto(any());
    }

    @Test
    @DisplayName("Should return user dto if user entity was found by user id")
    void test_18() {
        //given
        final var userDto = sampleUserDto
                .toBuilder()
                .userId(userIdI)
                .build();
        final var userEntity = sampleUserEntity
                .toBuilder()
                .userId(userIdI)
                .build();

        //when
        when(userRepository.findById(userIdI)).thenReturn(Optional.of(userEntity));
        when(userMapper.mapToUserDto(userEntity)).thenReturn(userDto);

        final var result = userService.getUserDtoByUserId(userIdI.toString());

        //then
        assertThat(result)
                .isNotNull()
                .isInstanceOf(UserDto.class)
                .isEqualTo(userDto);
        verify(userRepository).findById(userIdI);
        verify(userMapper).mapToUserDto(userEntity);
    }

    @Test
    @DisplayName("Should throw an exception if user entity wasn't found by user id")
    void test_19() {
        //when
        when(userRepository.findById(userIdI)).thenReturn(Optional.empty());

        final var expectedException = catchThrowable(() -> userService.getUserDtoByUserId(userIdI.toString()));

        //then
        assertThat(expectedException)
                .isNotNull()
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining(String.format("Can't find user with provided id: %s", userIdI));
        verify(userRepository).findById(userIdI);
        verify(userMapper, never()).mapToUserDto(any());
    }

}