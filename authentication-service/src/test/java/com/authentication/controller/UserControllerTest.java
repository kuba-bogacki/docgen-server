package com.authentication.controller;

import com.authentication.exception.UserNotFoundException;
import com.authentication.exception.UserUploadPhotoException;
import com.authentication.model.dto.UserDto;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static com.authentication.util.ApplicationConstants.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
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
        final MockMultipartFile file = new MockMultipartFile(LOADED_IMAGE, userPhotoFileName, MediaType.IMAGE_JPEG_VALUE, new byte[0]);

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
        final MockMultipartFile file = new MockMultipartFile(LOADED_IMAGE, userPhotoFileName, MediaType.IMAGE_JPEG_VALUE, new byte[0]);

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
        final MockMultipartFile file = new MockMultipartFile(LOADED_IMAGE, userPhotoFileName, MediaType.IMAGE_JPEG_VALUE, new byte[0]);

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
}