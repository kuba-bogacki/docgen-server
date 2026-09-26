package com.authentication.controller;

import com.authentication.exception.*;
import com.authentication.model.dto.UserPrincipalDto;
import com.authentication.security.AuthenticationRequest;
import com.authentication.security.RegisterRequest;
import com.authentication.service.AuthenticationService;
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
import org.springframework.test.web.servlet.MockMvc;

import static com.authentication.util.ApplicationConstants.API_VERSION;
import static com.authentication.util.ApplicationConstants.USER_EMAIL_HEADER;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(AuthenticationController.class)
class AuthenticationControllerTest extends AuthenticationSamples {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean private AuthenticationService authenticationService;

    @Test
    @SneakyThrows
    @DisplayName("When registration made correctly then should create new user")
    void test_01() {
        //when
        doNothing().when(authenticationService).register(any(RegisterRequest.class));

        mockMvc.perform(post(API_VERSION + "/authentication/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
        //then
                .andExpect(status().isCreated());
    }

    @Test
    @SneakyThrows
    @DisplayName("When user already exist then should handle exception")
    void test_02() {
        //when
        doThrow(UserAlreadyExistException.class).when(authenticationService).register(any(RegisterRequest.class));

        mockMvc.perform(post(API_VERSION + "/authentication/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
        //then
                .andExpect(status().isForbidden());
    }

    @Test
    @SneakyThrows
    @DisplayName("When couldn't send verification email then should handle exception")
    void test_03() {
        //when
        doThrow(UserAuthenticationException.class).when(authenticationService).register(any(RegisterRequest.class));

        mockMvc.perform(post(API_VERSION + "/authentication/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
        //then
                .andExpect(status().isForbidden());
    }

    @Test
    @SneakyThrows
    @DisplayName("When register request dto is invalid then should handle exception")
    void test_04() {
        //given
        final RegisterRequest invalidRequest = registerRequest.toBuilder()
                .userFirstNameI(StringUtils.EMPTY)
                .userLastNameI(StringUtils.EMPTY)
                .userEmail(StringUtils.EMPTY)
                .userPassword(StringUtils.EMPTY)
                .userGender(StringUtils.EMPTY)
                .termsAndCondition(false)
                .build();

        //when
        mockMvc.perform(post(API_VERSION + "/authentication/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
        //then
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    @DisplayName("When verification went successfully then should return tokens")
    void test_05() {
        //when
        doReturn(authenticationResponse).when(authenticationService).verifyUserRegistrationCode(registrationCode, authenticationRequest);

        mockMvc.perform(post(API_VERSION + "/authentication/verify/{registrationCode}", registrationCode)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authenticationRequest)))
        //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jwtToken").value(jwtToken))
                .andExpect(jsonPath("$.refreshToken").value(refreshToken));
    }

    @Test
    @SneakyThrows
    @DisplayName("When user not found then should handle exception")
    void test_06() {
        //when
        doThrow(UserNotFoundException.class).when(authenticationService).verifyUserRegistrationCode(registrationCode, authenticationRequest);

        mockMvc.perform(post(API_VERSION + "/authentication/verify/{registrationCode}", registrationCode)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authenticationRequest)))
        //then
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    @DisplayName("When user authentication failed then should handle exception")
    void test_07() {
        //when
        doThrow(UserAuthenticationException.class).when(authenticationService).verifyUserRegistrationCode(registrationCode, authenticationRequest);

        mockMvc.perform(post(API_VERSION + "/authentication/verify/{registrationCode}", registrationCode)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authenticationRequest)))
        //then
                .andExpect(status().isForbidden());
    }

    @Test
    @SneakyThrows
    @DisplayName("When user authorization failed then should handle exception")
    void test_08() {
        //when
        doThrow(UserAuthorizationException.class).when(authenticationService).verifyUserRegistrationCode(registrationCode, authenticationRequest);

        mockMvc.perform(post(API_VERSION + "/authentication/verify/{registrationCode}", registrationCode)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authenticationRequest)))
        //then
                .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @DisplayName("When verify authentication request dto is invalid then should handle exception")
    void test_09() {
        //given
        final AuthenticationRequest invalidRequest = authenticationRequest.toBuilder()
                .userEmail(StringUtils.EMPTY)
                .userPassword(StringUtils.EMPTY)
                .build();

        //when
        mockMvc.perform(post(API_VERSION + "/authentication/verify/{registrationCode}", registrationCode)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
        //then
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    @DisplayName("When authentication went successfully then should return tokens")
    void test_10() {
        //when
        doReturn(authenticationResponse).when(authenticationService).authenticate(authenticationRequest);

        mockMvc.perform(post(API_VERSION + "/authentication/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authenticationRequest)))
        //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jwtToken").value(jwtToken))
                .andExpect(jsonPath("$.refreshToken").value(refreshToken));
    }

    @Test
    @SneakyThrows
    @DisplayName("When user not found then should handle exception")
    void test_11() {
        //when
        doThrow(UserNotFoundException.class).when(authenticationService).authenticate(authenticationRequest);

        mockMvc.perform(post(API_VERSION + "/authentication/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authenticationRequest)))
        //then
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    @DisplayName("When user account disable then should handle exception")
    void test_12() {
        //when
        doThrow(UserAccountDisableException.class).when(authenticationService).authenticate(authenticationRequest);

        mockMvc.perform(post(API_VERSION + "/authentication/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authenticationRequest)))
        //then
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    @SneakyThrows
    @DisplayName("When login authentication request dto is invalid then should handle exception")
    void test_13() {
        //given
        final AuthenticationRequest invalidRequest = authenticationRequest.toBuilder()
                .userEmail(StringUtils.EMPTY)
                .userPassword(StringUtils.EMPTY)
                .build();

        //when
        mockMvc.perform(post(API_VERSION + "/authentication/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
        //then
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    @DisplayName("When user found by email then should refresh token")
    void test_14() {
        //when
        doNothing().when(authenticationService).refreshToken(userEmail);

        mockMvc.perform(post(API_VERSION + "/authentication/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_EMAIL_HEADER, userEmail))
                //then
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    @DisplayName("When user not found then should handle exception")
    void test_15() {
        //when
        doThrow(UserNotFoundException.class).when(authenticationService).refreshToken(userEmail);

        mockMvc.perform(post(API_VERSION + "/authentication/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_EMAIL_HEADER, userEmail))
        //then
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    @DisplayName("When couldn't send refresh token to client then should handle exception")
    void test_16() {
        //when
        doThrow(UserAuthenticationException.class).when(authenticationService).refreshToken(userEmail);

        mockMvc.perform(post(API_VERSION + "/authentication/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_EMAIL_HEADER, userEmail))
        //then
                .andExpect(status().isForbidden());
    }

    @Test
    @SneakyThrows
    @DisplayName("When company membership confirmation went successfully then should return tokens")
    void test_17() {
        //when
        doReturn(authenticationResponse).when(authenticationService).confirmCompanyMembership(companyId, authenticationRequest);

        mockMvc.perform(post(API_VERSION + "/authentication/confirm-membership/{companyId}", companyId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authenticationRequest)))
        //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jwtToken").value(jwtToken))
                .andExpect(jsonPath("$.refreshToken").value(refreshToken));
    }

    @Test
    @SneakyThrows
    @DisplayName("When user not found due confirming company membership then should handle exception")
    void test_18() {
        //when
        doThrow(UserNotFoundException.class).when(authenticationService).confirmCompanyMembership(companyId, authenticationRequest);

        mockMvc.perform(post(API_VERSION + "/authentication/confirm-membership/{companyId}", companyId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authenticationRequest)))
        //then
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    @DisplayName("When user authorization failed due confirming company membership then should handle exception")
    void test_19() {
        //when
        doThrow(UserAuthorizationException.class).when(authenticationService).confirmCompanyMembership(companyId, authenticationRequest);

        mockMvc.perform(post(API_VERSION + "/authentication/confirm-membership/{companyId}", companyId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authenticationRequest)))
        //then
                .andExpect(status().isUnauthorized());
    }

    @Test
    @SneakyThrows
    @DisplayName("When company membership confirmation authentication request dto is invalid then should handle exception")
    void test_20() {
        //given
        final AuthenticationRequest invalidRequest = authenticationRequest.toBuilder()
                .userEmail(StringUtils.EMPTY)
                .userPassword(StringUtils.EMPTY)
                .build();

        //when
        mockMvc.perform(post(API_VERSION + "/authentication/confirm-membership/{companyId}", companyId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
        //then
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    @DisplayName("When user found then should add principal number user")
    void test_21() {
        //when
        doNothing().when(authenticationService).addUserPrincipal(userPrincipalDto);

        mockMvc.perform(put(API_VERSION + "/authentication/add-user-principal")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userPrincipalDto)))
        //then
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    @DisplayName("When user not found due adding principal number then should handle exception")
    void test_22() {
        //when
        doThrow(UserNotFoundException.class).when(authenticationService).addUserPrincipal(userPrincipalDto);

        mockMvc.perform(put(API_VERSION + "/authentication/add-user-principal")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userPrincipalDto)))
        //then
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    @DisplayName("When user principal dto is invalid then should handle exception")
    void test_23() {
        //given
        final UserPrincipalDto invalidRequest = userPrincipalDto.toBuilder()
                .userId(StringUtils.EMPTY)
                .userPrincipal(StringUtils.EMPTY)
                .build();

        //when
        mockMvc.perform(put(API_VERSION + "/authentication/add-user-principal")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
        //then
                .andExpect(status().isBadRequest());
    }
}