package com.authentication.controller;

import com.authentication.exception.*;
import com.authentication.model.dto.MembershipDto;
import com.authentication.model.dto.PaymentDto;
import com.authentication.model.dto.UserDto;
import com.authentication.model.type.Gender;
import com.authentication.security.AuthenticationRequest;
import com.authentication.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.apache.commons.lang.StringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static com.authentication.util.ApplicationConstants.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(UserController.class)
class UserControllerTest extends AuthenticationSamples {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean private UserService userService;

    @Test
    @SneakyThrows
    @DisplayName("When user exist and email is valid then should return current user dto")
    void test_01() {
        //when
        doReturn(userDto).when(userService).getUserDtoByUserEmail(userEmail);

        mockMvc.perform(get(API_VERSION + "/authentication/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_EMAIL_HEADER, userEmail))
        //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.userFirstNameI").value(userFirstNameI))
                .andExpect(jsonPath("$.userLastNameI").value(userLastNameI))
                .andExpect(jsonPath("$.userEmail").value(userEmail))
                .andExpect(jsonPath("$.userGender").value(Gender.MALE.toString()))
                .andExpect(jsonPath("$.userVerificationCode").value(userVerificationCode))
                .andExpect(jsonPath("$.userPrincipal").value(userPrincipal));
    }

    @Test
    @SneakyThrows
    @DisplayName("When user not exist then should handle exception")
    void test_02() {
        //when
        doThrow(UserNotFoundException.class).when(userService).getUserDtoByUserEmail(userEmail);

        mockMvc.perform(get(API_VERSION + "/authentication/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_EMAIL_HEADER, userEmail))
        //then
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    @DisplayName("When user exist then should return user dto by email")
    void test_03() {
        //when
        doReturn(userDto).when(userService).getUserDtoByUserEmail(userEmail);

        mockMvc.perform(get(API_VERSION + "/authentication/user/{userEmail}", userEmail)
                        .contentType(MediaType.APPLICATION_JSON))
        //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.userFirstNameI").value(userFirstNameI))
                .andExpect(jsonPath("$.userLastNameI").value(userLastNameI))
                .andExpect(jsonPath("$.userEmail").value(userEmail))
                .andExpect(jsonPath("$.userGender").value(Gender.MALE.toString()))
                .andExpect(jsonPath("$.userVerificationCode").value(userVerificationCode))
                .andExpect(jsonPath("$.userPrincipal").value(userPrincipal));
    }

    @Test
    @SneakyThrows
    @DisplayName("When user not found by email then should handle exception")
    void test_04() {
        //when
        doThrow(UserNotFoundException.class).when(userService).getUserDtoByUserEmail(userEmail);

        mockMvc.perform(get(API_VERSION + "/authentication/user/{userEmail}", userEmail)
                        .contentType(MediaType.APPLICATION_JSON))
        //then
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    @DisplayName("When user exist then should return user dto by id")
    void test_05() {
        //when
        doReturn(userDto).when(userService).getUserDtoByUserId(userId);

        mockMvc.perform(get(API_VERSION + "/authentication/get-by-id/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
        //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.userFirstNameI").value(userFirstNameI))
                .andExpect(jsonPath("$.userLastNameI").value(userLastNameI))
                .andExpect(jsonPath("$.userEmail").value(userEmail))
                .andExpect(jsonPath("$.userGender").value(Gender.MALE.toString()))
                .andExpect(jsonPath("$.userVerificationCode").value(userVerificationCode))
                .andExpect(jsonPath("$.userPrincipal").value(userPrincipal));
    }

    @Test
    @SneakyThrows
    @DisplayName("When user not found by id then should handle exception")
    void test_06() {
        //when
        doThrow(UserNotFoundException.class).when(userService).getUserDtoByUserId(userId);

        mockMvc.perform(get(API_VERSION + "/authentication/get-by-id/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
        //then
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    @DisplayName("When user exist then should return user id")
    void test_07() {
        //when
        doReturn(userDto).when(userService).getUserDtoByUserEmail(userEmail);

        mockMvc.perform(get(API_VERSION + "/authentication/get-id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_EMAIL_HEADER, userEmail))
        //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(userId));    }

    @Test
    @SneakyThrows
    @DisplayName("When user not found by user email then should handle exception")
    void test_08() {
        //when
        doThrow(UserNotFoundException.class).when(userService).getUserDtoByUserEmail(userEmail);

        mockMvc.perform(get(API_VERSION + "/authentication/get-id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_EMAIL_HEADER, userEmail))
        //then
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    @DisplayName("When user exist and user dto is valid then should return updated user dto")
    void test_09() {
        //given
        final UserDto updatedDto = userDto.toBuilder()
                .userLastNameI(updatedUserLastNameI)
                .build();

        //when
        doReturn(updatedDto).when(userService).updateUserData(userDto);

        mockMvc.perform(put(API_VERSION + "/authentication/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
        //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userLastNameI").value(updatedUserLastNameI));
    }

    @Test
    @SneakyThrows
    @DisplayName("When valid user dto but user not exist then should handle exception")
    void test_10() {
        //when
        doThrow(UserNotFoundException.class).when(userService).updateUserData(userDto);

        mockMvc.perform(put(API_VERSION + "/authentication/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
        //then
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    @DisplayName("When valid user email and multipart file then should return file name")
    void test_11() {
        //given
        final MockMultipartFile file = new MockMultipartFile(LOADED_IMAGE_PARAM, userPhotoFileName, MediaType.IMAGE_JPEG_VALUE, new byte[0]);

        //when
        doReturn(userPhotoFileName).when(userService).uploadNewUserPhoto(file, userEmail);

        mockMvc.perform(multipart(API_VERSION + "/authentication/photo")
                        .file(file)
                        .header(USER_EMAIL_HEADER, userEmail))
        //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(userPhotoFileName));
    }

    @Test
    @SneakyThrows
    @DisplayName("When user not found due uploading photo then should handle exception")
    void test_12() {
        //given
        final MockMultipartFile file = new MockMultipartFile(LOADED_IMAGE_PARAM, userPhotoFileName, MediaType.IMAGE_JPEG_VALUE, new byte[0]);

        //when
        doThrow(UserNotFoundException.class).when(userService).uploadNewUserPhoto(file, userEmail);

        mockMvc.perform(multipart(API_VERSION + "/authentication/photo")
                        .file(file)
                        .header(USER_EMAIL_HEADER, userEmail))
        //then
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    @DisplayName("When error occur due uploading photo then should handle exception")
    void test_13() {
        //given
        final MockMultipartFile file = new MockMultipartFile(LOADED_IMAGE_PARAM, userPhotoFileName, MediaType.IMAGE_JPEG_VALUE, new byte[0]);

        //when
        doThrow(UserUploadPhotoException.class).when(userService).uploadNewUserPhoto(file, userEmail);

        mockMvc.perform(multipart(API_VERSION + "/authentication/photo")
                        .file(file)
                        .header(USER_EMAIL_HEADER, userEmail))
        //then
                .andExpect(status().isInternalServerError());
    }

    @Test
    @SneakyThrows
    @DisplayName("When company member and not belong to company then should return user dto")
    void test_14() {
        //when
        doReturn(userDto).when(userService).getUserNotCompanyMember(companyId, userEmail);

        mockMvc.perform(post(API_VERSION + "/authentication/company-member/{userEmail}", userEmail)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(companyId))
        //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.userFirstNameI").value(userFirstNameI))
                .andExpect(jsonPath("$.userLastNameI").value(userLastNameI))
                .andExpect(jsonPath("$.userEmail").value(userEmail))
                .andExpect(jsonPath("$.userGender").value(Gender.MALE.toString()))
                .andExpect(jsonPath("$.userVerificationCode").value(userVerificationCode))
                .andExpect(jsonPath("$.userPrincipal").value(userPrincipal));
    }

    @Test
    @SneakyThrows
    @DisplayName("When company member not found then should handle exception")
    void test_15() {
        //when
        doThrow(UserNotFoundException.class).when(userService).getUserNotCompanyMember(companyId, userEmail);

        mockMvc.perform(post(API_VERSION + "/authentication/company-member/{userEmail}", userEmail)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(companyId))
        //then
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    @DisplayName("When company member belong to company then should handle exception")
    void test_16() {
        //when
        doThrow(UserAlreadyExistException.class).when(userService).getUserNotCompanyMember(companyId, userEmail);

        mockMvc.perform(post(API_VERSION + "/authentication/company-member/{userEmail}", userEmail)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(companyId))
        //then
                .andExpect(status().isForbidden());
    }

    @Test
    @SneakyThrows
    @DisplayName("When payment session created successfully then should return payment dto")
    void test_17() {
        //when
        doReturn(paymentIntentDto).when(userService).createPaymentSession(any(PaymentDto.class), eq(userEmail));

        mockMvc.perform(post(API_VERSION + "/authentication/create-payment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_EMAIL_HEADER, userEmail)
                        .content(objectMapper.writeValueAsString(paymentDto)))
        //then
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.paymentIntentId").value(paymentIntentId))
                .andExpect(jsonPath("$.paymentClientSecret").value(paymentClientSecret));
    }

    @Test
    @SneakyThrows
    @DisplayName("When error occur due payment session creation then should handle exception")
    void test_18() {
        //when
        doThrow(UserPaymentSessionException.class).when(userService).createPaymentSession(any(PaymentDto.class), eq(userEmail));

        mockMvc.perform(post(API_VERSION + "/authentication/create-payment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_EMAIL_HEADER, userEmail)
                        .content(objectMapper.writeValueAsString(paymentDto)))
        //then
                .andExpect(status().isNotAcceptable());
    }

    @Test
    @SneakyThrows
    @DisplayName("When payment dto is invalid then should handle exception")
    void test_19() {
        //given
        final PaymentDto invalidDto = paymentDto.toBuilder()
                .membership(null)
                .packagePrice(null)
                .priceCurrency(null)
                .build();

        //when
        mockMvc.perform(post(API_VERSION + "/authentication/create-payment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_EMAIL_HEADER, userEmail)
                        .content(objectMapper.writeValueAsString(invalidDto)))
        //then
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    @DisplayName("When payment session cancelled successfully then should return payment status")
    void test_20() {
        //when
        doReturn(paymentStatus).when(userService).cancelPaymentSession(paymentIntentId);

        mockMvc.perform(delete(API_VERSION + "/authentication/cancel-payment/{paymentIntentId}", paymentIntentId)
                        .contentType(MediaType.APPLICATION_JSON))
        //then
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$").value(paymentStatus));
    }

    @Test
    @SneakyThrows
    @DisplayName("When error occur due payment session cancelled successfully then should handle exception")
    void test_21() {
        //when
        doThrow(UserPaymentSessionException.class).when(userService).cancelPaymentSession(paymentIntentId);

        mockMvc.perform(delete(API_VERSION + "/authentication/cancel-payment/{paymentIntentId}", paymentIntentId)
                        .contentType(MediaType.APPLICATION_JSON))
        //then
                .andExpect(status().isNotAcceptable());
    }

    @Test
    @SneakyThrows
    @DisplayName("When membership dto is valid then should update user membership")
    void test_22() {
        //when
        doNothing().when(userService).updateUserMembership(membershipDto.getMembership(), userEmail);

        mockMvc.perform(put(API_VERSION + "/authentication/update-user-membership")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_EMAIL_HEADER, userEmail)
                        .content(objectMapper.writeValueAsString(membershipDto)))
        //then
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    @DisplayName("When user not found due updating user membership then should handle exception")
    void test_23() {
        //when
        doThrow(UserNotFoundException.class).when(userService).updateUserMembership(membershipDto.getMembership(), userEmail);

        mockMvc.perform(put(API_VERSION + "/authentication/update-user-membership")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_EMAIL_HEADER, userEmail)
                        .content(objectMapper.writeValueAsString(membershipDto)))
        //then
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    @DisplayName("When membership dto is invalid then should handle exception")
    void test_24() {
        //given
        final MembershipDto invalidDto = membershipDto.toBuilder()
                .membership(null)
                .build();

        //when
        mockMvc.perform(put(API_VERSION + "/authentication/update-user-membership")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_EMAIL_HEADER, userEmail)
                        .content(objectMapper.writeValueAsString(invalidDto)))
        //then
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    @DisplayName("When user reset password then should return account non locked equals false")
    void test_25() {
        //when
        doReturn(false).when(userService).sendVerificationEmail(userEmail);

        mockMvc.perform(post(API_VERSION + "/authentication/send-email-to-reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_EMAIL_HEADER, userEmail)
                        .param(USER_EMAIL_PARAM, userEmail))
        //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(false));
    }

    @Test
    @SneakyThrows
    @DisplayName("When user not found due resetting password then should handle exception")
    void test_26() {
        //when
        doThrow(UserNotFoundException.class).when(userService).sendVerificationEmail(userEmail);

        mockMvc.perform(post(API_VERSION + "/authentication/send-email-to-reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_EMAIL_HEADER, userEmail)
                        .param(USER_EMAIL_PARAM, userEmail))
        //then
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    @DisplayName("When error occur due sending reset password email then should handle exception")
    void test_27() {
        //when
        doThrow(UserWebClientException.class).when(userService).sendVerificationEmail(userEmail);

        mockMvc.perform(post(API_VERSION + "/authentication/send-email-to-reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_EMAIL_HEADER, userEmail)
                        .param(USER_EMAIL_PARAM, userEmail))
        //then
                .andExpect(status().isServiceUnavailable());
    }

    @Test
    @SneakyThrows
    @DisplayName("When authentication request is valid then should return account non locked equals true")
    void test_28() {
        //when
        doReturn(true).when(userService).resetUserPassword(userVerificationCode, authenticationRequest);

        mockMvc.perform(post(API_VERSION + "/authentication/reset-password/{verificationCode}", userVerificationCode)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authenticationRequest)))
        //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
    }

    @Test
    @SneakyThrows
    @DisplayName("When user not found due resetting password then should handle exception")
    void test_29() {
        //when
        doThrow(UserNotFoundException.class).when(userService).resetUserPassword(userVerificationCode, authenticationRequest);

        mockMvc.perform(post(API_VERSION + "/authentication/reset-password/{verificationCode}", userVerificationCode)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authenticationRequest)))
        //then
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    @DisplayName("When user verification code is not match then should handle exception")
    void test_30() {
        //when
        doThrow(UserAuthenticationException.class).when(userService).resetUserPassword(userVerificationCode, authenticationRequest);

        mockMvc.perform(post(API_VERSION + "/authentication/reset-password/{verificationCode}", userVerificationCode)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authenticationRequest)))
        //then
                .andExpect(status().isForbidden());
    }

    @Test
    @SneakyThrows
    @DisplayName("When authentication request is invalid then should handle exception")
    void test_31() {
        //given
        final AuthenticationRequest invalidRequest = AuthenticationRequest.builder()
                .userEmail(StringUtils.EMPTY)
                .userPassword(StringUtils.EMPTY)
                .build();

        //when
        mockMvc.perform(post(API_VERSION + "/authentication/reset-password/{verificationCode}", userVerificationCode)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
        //then
                .andExpect(status().isBadRequest());
    }
}