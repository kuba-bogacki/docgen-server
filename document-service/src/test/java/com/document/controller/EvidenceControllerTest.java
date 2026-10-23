package com.document.controller;

import com.document.exception.CompanyNotFoundException;
import com.document.exception.EvidenceNotFoundException;
import com.document.exception.UserNotFoundException;
import com.document.model.dto.FinancialStatementDto;
import com.document.service.EvidenceService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.document.util.ApplicationConstants.API_VERSION;
import static com.document.util.ApplicationConstants.USER_EMAIL_HEADER;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(EvidenceController.class)
class EvidenceControllerTest extends Samples {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockitoBean private EvidenceService evidenceService;

    @Test
    @SneakyThrows
    @DisplayName("When evidence has been found by id then should return evidence details dto")
    void test_01() {
        //when
        doReturn(evidenceDetailsDto).when(evidenceService).getEvidenceDetailsById(evidenceId);

        mockMvc.perform(get(API_VERSION + "/document/{evidenceId}", evidenceId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(evidenceDetailsDto)))
        //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.evidenceId").value(evidenceId))
                .andExpect(jsonPath("$.evidenceName").value(evidenceName))
                .andExpect(jsonPath("$.evidenceType").value(evidenceType))
                .andExpect(jsonPath("$.companyId").value(companyId))
                .andExpect(jsonPath("$.createDateTime").value(createDateTime));
    }

    @Test
    @SneakyThrows
    @DisplayName("When evidence not found by evidence id then should handle exception")
    void test_02() {
        //when
        doThrow(EvidenceNotFoundException.class).when(evidenceService).getEvidenceDetailsById(evidenceId);

        mockMvc.perform(get(API_VERSION + "/document/{evidenceId}", evidenceId)
                        .contentType(MediaType.APPLICATION_JSON))
        //then
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    @DisplayName("When financial statement dto is valid then should create new financial statement entity")
    void test_03() {
        //when
        doNothing().when(evidenceService).createFinancialStatement(financialStatementDto, userEmail);

        mockMvc.perform(post(API_VERSION + "/document/create-financial-statement")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_EMAIL_HEADER, userEmail)
                        .content(objectMapper.writeValueAsString(financialStatementDto)))
        //then
                .andExpect(status().isCreated());
    }

    @Test
    @SneakyThrows
    @DisplayName("When financial statement dto is invalid then should handle exception")
    void test_04() {
        //given
        final FinancialStatementDto invalidFinancialStatementDto = financialStatementDto.toBuilder()
                .companyId(StringUtils.EMPTY)
                .periodStartDate(periodStartDate)
                .periodEndDate(periodEndDate)
                .companyEquity(null)
                .companyTotalSum(null)
                .companyNetProfit(null)
                .companyNetIncrease(null)
                .managementBoardPresident(StringUtils.EMPTY)
                .supervisoryBoardChairman(StringUtils.EMPTY)
                .build();

        //when
        mockMvc.perform(post(API_VERSION + "/document/create-financial-statement")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_EMAIL_HEADER, userEmail)
                        .content(objectMapper.writeValueAsString(invalidFinancialStatementDto)))
        //then
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    @DisplayName("When user not found due creating new financial statement then should handle exception")
    void test_05() {
        //when
        doThrow(UserNotFoundException.class).when(evidenceService).createFinancialStatement(financialStatementDto, userEmail);

        mockMvc.perform(post(API_VERSION + "/document/create-financial-statement")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_EMAIL_HEADER, userEmail)
                        .content(objectMapper.writeValueAsString(financialStatementDto)))
        //then
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    @DisplayName("When current company not found due creating new financial statement then should handle exception")
    void test_06() {
        //when
        doThrow(CompanyNotFoundException.class).when(evidenceService).createFinancialStatement(financialStatementDto, userEmail);

        mockMvc.perform(post(API_VERSION + "/document/create-financial-statement")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_EMAIL_HEADER, userEmail)
                        .content(objectMapper.writeValueAsString(financialStatementDto)))
        //then
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    @DisplayName("When evidences has been found by id then should return evidence details dto list")
    void test_07() {
        //when
        doReturn(List.of(evidenceDetailsDto)).when(evidenceService).getAllCompanyEvidences(companyId);

        mockMvc.perform(get(API_VERSION + "/document/get-all/{companyId}", companyId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(evidenceDetailsDto)))
        //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].evidenceId").value(evidenceId))
                .andExpect(jsonPath("$[0].evidenceName").value(evidenceName))
                .andExpect(jsonPath("$[0].evidenceType").value(evidenceType))
                .andExpect(jsonPath("$[0].companyId").value(companyId))
                .andExpect(jsonPath("$[0].createDateTime").value(createDateTime));
    }

    @Test
    @SneakyThrows
    @DisplayName("When evidence has been found by id then should removed evidence")
    void test_08() {
        //when
        doNothing().when(evidenceService).deleteEvidenceById(evidenceId);

        mockMvc.perform(delete(API_VERSION + "/document/{evidenceId}", evidenceId)
                        .contentType(MediaType.APPLICATION_JSON))
        //then
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    @DisplayName("When evidence not found by evidence id due deleting evidence then should handle exception")
    void test_09() {
        //when
        doThrow(EvidenceNotFoundException.class).when(evidenceService).deleteEvidenceById(evidenceId);

        mockMvc.perform(delete(API_VERSION + "/document/{evidenceId}", evidenceId)
                        .contentType(MediaType.APPLICATION_JSON))
        //then
                .andExpect(status().isNotFound());
    }
}