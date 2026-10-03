package com.company.controller;

import com.company.exception.CompanyMemberAdditionException;
import com.company.exception.CompanyNonExistException;
import com.company.service.CompanyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.company.util.ApplicationConstants.API_VERSION;
import static com.company.util.ApplicationConstants.USER_EMAIL_HEADER;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(CompanyController.class)
class CompanyControllerTest extends Samples {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockitoBean private CompanyService companyService;

    @Test
    @SneakyThrows
    @DisplayName("When company already exist then should return true")
    void test_01() {
        //when
        doReturn(true).when(companyService).checkIfCompanyAlreadyExist(companyKrsNumber);

        mockMvc.perform(get(API_VERSION + "/company/exist/{companyKrsNumber}", companyKrsNumber)
                        .contentType(MediaType.APPLICATION_JSON))
        //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
    }

    @Test
    @SneakyThrows
    @DisplayName("When company by company name found then should return company dto")
    void test_02() {
        //when
        doReturn(companyDto).when(companyService).getCompanyByName(companyName);

        mockMvc.perform(get(API_VERSION + "/company/{companyName}", companyName)
                        .contentType(MediaType.APPLICATION_JSON))
        //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.companyId").value(companyId))
                .andExpect(jsonPath("$.companyName").value(companyName))
                .andExpect(jsonPath("$.companyKrsNumber").value(companyKrsNumber))
                .andExpect(jsonPath("$.companyRegonNumber").value(companyRegonNumber))
                .andExpect(jsonPath("$.companyNipNumber").value(companyNipNumber))
                .andExpect(jsonPath("$.companyRegistrationDate").value(companyRegistrationDateStringFormat))
                .andExpect(jsonPath("$.companyAddressDto").value(companyAddressDto))
                .andExpect(jsonPath("$.companyShareCapital").value(companyShareCapital));
    }

    @Test
    @SneakyThrows
    @DisplayName("When company not found by company name then should handle exception")
    void test_03() {
        //when
        doThrow(CompanyNonExistException.class).when(companyService).getCompanyByName(companyName);

        mockMvc.perform(get(API_VERSION + "/company/{companyName}", companyName)
                        .contentType(MediaType.APPLICATION_JSON))
        //then
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    @DisplayName("When company by company id found then should return company dto")
    void test_04() {
        //when
        doReturn(companyDto).when(companyService).getCompanyByCompanyId(companyId);

        mockMvc.perform(get(API_VERSION + "/company/details/{companyId}", companyId)
                        .contentType(MediaType.APPLICATION_JSON))
        //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.companyId").value(companyId))
                .andExpect(jsonPath("$.companyName").value(companyName))
                .andExpect(jsonPath("$.companyKrsNumber").value(companyKrsNumber))
                .andExpect(jsonPath("$.companyRegonNumber").value(companyRegonNumber))
                .andExpect(jsonPath("$.companyNipNumber").value(companyNipNumber))
                .andExpect(jsonPath("$.companyRegistrationDate").value(companyRegistrationDateStringFormat))
                .andExpect(jsonPath("$.companyAddressDto").value(companyAddressDto))
                .andExpect(jsonPath("$.companyShareCapital").value(companyShareCapital));
    }

    @Test
    @SneakyThrows
    @DisplayName("When company not found by company id then should handle exception")
    void test_05() {
        //when
        doThrow(CompanyNonExistException.class).when(companyService).getCompanyByCompanyId(companyId);

        mockMvc.perform(get(API_VERSION + "/company/details/{companyId}", companyId)
                        .contentType(MediaType.APPLICATION_JSON))
        //then
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    @DisplayName("When company by company krs number found then should return company dto")
    void test_06() {
        //when
        doReturn(companyDto).when(companyService).getCompanyByCompanyKrsNumber(companyKrsNumber);

        mockMvc.perform(get(API_VERSION + "/company/get-by-krs/{krsNumber}", companyKrsNumber)
                        .contentType(MediaType.APPLICATION_JSON))
        //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.companyId").value(companyId))
                .andExpect(jsonPath("$.companyName").value(companyName))
                .andExpect(jsonPath("$.companyKrsNumber").value(companyKrsNumber))
                .andExpect(jsonPath("$.companyRegonNumber").value(companyRegonNumber))
                .andExpect(jsonPath("$.companyNipNumber").value(companyNipNumber))
                .andExpect(jsonPath("$.companyRegistrationDate").value(companyRegistrationDateStringFormat))
                .andExpect(jsonPath("$.companyAddressDto").value(companyAddressDto))
                .andExpect(jsonPath("$.companyShareCapital").value(companyShareCapital));
    }

    @Test
    @SneakyThrows
    @DisplayName("When company not found by company krs number then should handle exception")
    void test_07() {
        //when
        doThrow(CompanyNonExistException.class).when(companyService).getCompanyByCompanyKrsNumber(companyKrsNumber);

        mockMvc.perform(get(API_VERSION + "/company/get-by-krs/{krsNumber}", companyKrsNumber)
                        .contentType(MediaType.APPLICATION_JSON))
        //then
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    @DisplayName("When company dto is valid then should return created company dto")
    void test_08() {
        //when
        doReturn(companyDto).when(companyService).createCompany(companyDto, userEmail);

        mockMvc.perform(post(API_VERSION + "/company/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_EMAIL_HEADER, userEmail)
                        .content(objectMapper.writeValueAsString(companyDto)))
        //then
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.companyId").value(companyId))
                .andExpect(jsonPath("$.companyName").value(companyName))
                .andExpect(jsonPath("$.companyKrsNumber").value(companyKrsNumber))
                .andExpect(jsonPath("$.companyRegonNumber").value(companyRegonNumber))
                .andExpect(jsonPath("$.companyNipNumber").value(companyNipNumber))
                .andExpect(jsonPath("$.companyRegistrationDate").value(companyRegistrationDateStringFormat))
                .andExpect(jsonPath("$.companyAddressDto").value(companyAddressDto))
                .andExpect(jsonPath("$.companyShareCapital").value(companyShareCapital));
    }

    @Test
    @SneakyThrows
    @DisplayName("When company dto is valid then should return updated company dto")
    void test_09() {
        //when
        doReturn(companyDto).when(companyService).updateCompany(companyDto);

        mockMvc.perform(put(API_VERSION + "/company/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(companyDto)))
        //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.companyId").value(companyId))
                .andExpect(jsonPath("$.companyName").value(companyName))
                .andExpect(jsonPath("$.companyKrsNumber").value(companyKrsNumber))
                .andExpect(jsonPath("$.companyRegonNumber").value(companyRegonNumber))
                .andExpect(jsonPath("$.companyNipNumber").value(companyNipNumber))
                .andExpect(jsonPath("$.companyRegistrationDate").value(companyRegistrationDateStringFormat))
                .andExpect(jsonPath("$.companyAddressDto").value(companyAddressDto))
                .andExpect(jsonPath("$.companyShareCapital").value(companyShareCapital));
    }

    @Test
    @SneakyThrows
    @DisplayName("Should return current user companies dto list")
    void test_10() {
        //when
        doReturn(List.of(companyDto)).when(companyService).getCurrentUserCompanies(userEmail);

        mockMvc.perform(get(API_VERSION + "/company/current-user-companies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_EMAIL_HEADER, userEmail))
        //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].companyId").value(companyId))
                .andExpect(jsonPath("$[0].companyName").value(companyName))
                .andExpect(jsonPath("$[0].companyKrsNumber").value(companyKrsNumber))
                .andExpect(jsonPath("$[0].companyRegonNumber").value(companyRegonNumber))
                .andExpect(jsonPath("$[0].companyNipNumber").value(companyNipNumber))
                .andExpect(jsonPath("$[0].companyRegistrationDate").value(companyRegistrationDateStringFormat))
                .andExpect(jsonPath("$[0].companyAddressDto").value(companyAddressDto))
                .andExpect(jsonPath("$[0].companyShareCapital").value(companyShareCapital));
    }

    @Test
    @SneakyThrows
    @DisplayName("When company id is valid then should return company members id set")
    void test_11() {
        //when
        doReturn(Set.of(UUID.fromString(memberId))).when(companyService).getCompanyMemberIdList(companyId);

        mockMvc.perform(get(API_VERSION + "/company/company-members/{companyId}", companyId)
                        .contentType(MediaType.APPLICATION_JSON))
        //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value(memberId));
    }

    @Test
    @SneakyThrows
    @DisplayName("When company not found due getting members list then should handle exception")
    void test_12() {
        //when
        doThrow(CompanyNonExistException.class).when(companyService).getCompanyMemberIdList(companyId);

        mockMvc.perform(get(API_VERSION + "/company/company-members/{companyId}", companyId)
                        .contentType(MediaType.APPLICATION_JSON))
        //then
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    @DisplayName("When company exist then should return company members user dto list")
    void test_13() {
        //when
        doReturn(List.of(userDto)).when(companyService).getDetailMembersList(companyId, userEmail);

        mockMvc.perform(get(API_VERSION + "/company/members-details/{companyId}", companyId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_EMAIL_HEADER, userEmail))
        //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userFirstNameI").value(userFirstNameI))
                .andExpect(jsonPath("$[0].userFirstNameII").value(userFirstNameII))
                .andExpect(jsonPath("$[0].userLastNameI").value(userLastNameI))
                .andExpect(jsonPath("$[0].userLastNameII").value(userLastNameII))
                .andExpect(jsonPath("$[0].userPhotoFileName").value(userPhotoFileName))
                .andExpect(jsonPath("$[0].userEmail").value(userEmail));
    }

    @Test
    @SneakyThrows
    @DisplayName("When company not found due getting company members user dto list then should handle exception")
    void test_14() {
        //when
        doThrow(CompanyNonExistException.class).when(companyService).getDetailMembersList(companyId, userEmail);

        mockMvc.perform(get(API_VERSION + "/company/members-details/{companyId}", companyId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_EMAIL_HEADER, userEmail))
        //then
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    @DisplayName("When company exist then should add new member to company")
    void test_15() {
        //when
        doNothing().when(companyService).addNewMemberToCompany(companyId, userId);

        mockMvc.perform(put(API_VERSION + "/company/add-new-member/{companyId}", companyId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userId))
        //then
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    @DisplayName("When company not exist due adding new member to company then should handle exception")
    void test_16() {
        //when
        doThrow(CompanyMemberAdditionException.class).when(companyService).addNewMemberToCompany(companyId, userId);

        mockMvc.perform(put(API_VERSION + "/company/add-new-member/{companyId}", companyId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userId))
        //then
                .andExpect(status().isForbidden());
    }
}