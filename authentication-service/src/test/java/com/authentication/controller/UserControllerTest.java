package com.authentication.controller;

import com.authentication.exception.UserNotFoundException;
import com.authentication.model.type.Gender;
import com.authentication.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
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
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    @DisplayName("When user not not found by email then should handle exception")
    void test_04() {
        //when
        doThrow(UserNotFoundException.class).when(userService).getUserDtoByUserEmail(userEmail);

        mockMvc.perform(get(API_VERSION + "/authentication/user/{userEmail}", userEmail)
                        .contentType(MediaType.APPLICATION_JSON))
        //then
                .andExpect(status().isNotFound());
    }
}