package com.document.service;

import com.document.config.docx.DocxReaderConfiguration;
import com.document.exception.CompanyNotFoundException;
import com.document.exception.DocxEvidenceReaderException;
import com.document.exception.EvidenceNotFoundException;
import com.document.exception.UserNotFoundException;
import com.document.mapper.EvidenceMapper;
import com.document.model.Evidence;
import com.document.model.dto.CompanyDto;
import com.document.model.dto.UserDto;
import com.document.repository.EvidenceRepository;
import com.document.service.implementation.EvidenceServiceImplementation;
import org.apache.commons.lang.StringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.document.model.type.EvidenceType.FINANCIAL_STATEMENT;
import static com.document.util.ApplicationConstants.FINANCIAL_STATEMENT_FILE_NAME;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EvidenceServiceImplementationTest extends EvidenceSamples {

    @SuppressWarnings("rawtypes")
    @Mock private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
    @SuppressWarnings("rawtypes")
    @Mock private WebClient.RequestHeadersSpec requestHeadersSpec;
    @Mock private WebClient.ResponseSpec responseSpec;
    @Mock private WebClient.Builder webClientBuilder;
    @Mock private WebClient webClient;
    @Mock private DocxReaderConfiguration docxReader;
    @Mock private EvidenceRepository evidenceRepository;
    @Mock private EvidenceMapper evidenceMapper;
    @Captor private ArgumentCaptor<Evidence> evidenceCaptor;
    @Captor private ArgumentCaptor<Map<String, String>> placeholdersCaptor;
    @InjectMocks private EvidenceServiceImplementation evidenceService;

    @Test
    @DisplayName("Should delete evidence if evidence id is provide and evidence exist")
    void test_01() {
        //when
        when(evidenceRepository.findByEvidenceId(evidenceIdNo1)).thenReturn(evidenceEntity);
        doNothing().when(evidenceRepository).delete(evidenceEntity);

        evidenceService.deleteEvidenceById(evidenceIdNo1);

        //then
        verify(evidenceRepository, times(1)).findByEvidenceId(evidenceIdNo1);
        verify(evidenceRepository, times(1)).delete(evidenceEntity);
    }

    @Test
    @DisplayName("Should throw an exception if evidence id is provide but evidence not exist")
    void test_02() {
        //when
        when(evidenceRepository.findByEvidenceId(evidenceIdNo1)).thenReturn(null);

        final var expectedException = catchThrowable(() -> evidenceService.deleteEvidenceById(evidenceIdNo1));

        //then
        verify(evidenceRepository, times(1)).findByEvidenceId(evidenceIdNo1);
        verify(evidenceRepository, never()).delete(any(Evidence.class));
        assertThat(expectedException)
                .isInstanceOf(EvidenceNotFoundException.class)
                .hasMessageContaining("Evidence with provided id not exist");
    }

    @Test
    @SuppressWarnings("unchecked")
    @DisplayName("Should create and save financial statement if necessary data was provide")
    void test_03() {
        //given
        final var sampleJwtToken = "sampleJwtToken";
        final var authenticationServiceUri = "http://authentication-service/v1.0/authentication/user";
        final var companyServiceUri = "http://company-service/v1.0/company/details/" + companyIdNo1;

        //when
        when(webClientBuilder.filter(any())).thenReturn(webClientBuilder);
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(authenticationServiceUri)).thenReturn(requestHeadersSpec);
        when(requestHeadersUriSpec.uri(companyServiceUri)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(UserDto.class)).thenReturn(Mono.just(userDto));
        when(responseSpec.bodyToMono(CompanyDto.class)).thenReturn(Mono.just(companyDto));
        when(docxReader.generateDocument(anyMap(), eq(FINANCIAL_STATEMENT_FILE_NAME))).thenReturn(documentDto);
        when(evidenceRepository.save(any(Evidence.class))).thenReturn(evidenceEntity);

        evidenceService.createFinancialStatement(financialStatementDto, sampleJwtToken);

        //then
        verify(docxReader, times(1)).generateDocument(placeholdersCaptor.capture(), eq(FINANCIAL_STATEMENT_FILE_NAME));
        verify(evidenceRepository, times(1)).save(evidenceCaptor.capture());
        assertAll(
                () -> assertThat(placeholdersCaptor.getValue().get("supervisoryBoardMembers"))
                        .isEqualTo(String.format("%s, \n%s", supervisoryBoardMembersNo1, supervisoryBoardMembersNo2)),
                () -> assertThat(placeholdersCaptor.getValue().get("addressLocalNumber"))
                        .isEqualTo(StringUtils.EMPTY),
                () -> assertThat(placeholdersCaptor.getValue().get("currentUser"))
                        .isEqualTo(String.format("%s %s %s", userFirstNameI, userFirstNameII, userLastNameI)),
                () -> assertThat(evidenceCaptor.getValue().getEvidenceType())
                        .isEqualTo(FINANCIAL_STATEMENT),
                () -> assertThat(evidenceCaptor.getValue().getEvidenceName())
                        .isEqualTo("Financial_statement_Allegro_sp._z_o.o.")
        );
    }

    @Test
    @SuppressWarnings("unchecked")
    @DisplayName("Should throw an exception if could not get current user dto")
    void test_04() {
        //given
        final var sampleJwtToken = "sampleJwtToken";
        final var authenticationServiceUri = "http://authentication-service/v1.0/authentication/user";

        //when
        when(webClientBuilder.filter(any())).thenReturn(webClientBuilder);
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(authenticationServiceUri)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(UserDto.class)).thenReturn(Mono.empty());

        final var expectedException =
                catchException(() -> evidenceService.createFinancialStatement(financialStatementDto, sampleJwtToken));

        //then
        verify(docxReader, never()).generateDocument(anyMap(), eq(FINANCIAL_STATEMENT_FILE_NAME));
        verify(evidenceRepository, never()).save(any(Evidence.class));
        assertThat(expectedException)
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("Couldn't find current user with provided id in database");
    }

    @Test
    @SuppressWarnings("unchecked")
    @DisplayName("Should throw an exception if could not get current company dto")
    void test_05() {
        //given
        final var sampleJwtToken = "sampleJwtToken";
        final var authenticationServiceUri = "http://authentication-service/v1.0/authentication/user";
        final var companyServiceUri = "http://company-service/v1.0/company/details/" + companyIdNo1;

        //when
        when(webClientBuilder.filter(any())).thenReturn(webClientBuilder);
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(authenticationServiceUri)).thenReturn(requestHeadersSpec);
        when(requestHeadersUriSpec.uri(companyServiceUri)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(UserDto.class)).thenReturn(Mono.just(userDto));
        when(responseSpec.bodyToMono(CompanyDto.class)).thenReturn(Mono.empty());

        final var expectedException =
                catchException(() -> evidenceService.createFinancialStatement(financialStatementDto, sampleJwtToken));

        //then
        verify(docxReader, never()).generateDocument(anyMap(), eq(FINANCIAL_STATEMENT_FILE_NAME));
        verify(evidenceRepository, never()).save(any(Evidence.class));
        assertThat(expectedException)
                .isInstanceOf(CompanyNotFoundException.class)
                .hasMessageContaining("Couldn't find company with provided id in database");
    }

    @Test
    @SuppressWarnings("unchecked")
    @DisplayName("Should throw an exception if could not generate company financial statement")
    void test_06() {
        //given
        final var sampleJwtToken = "sampleJwtToken";
        final var authenticationServiceUri = "http://authentication-service/v1.0/authentication/user";
        final var companyServiceUri = "http://company-service/v1.0/company/details/" + companyIdNo1;

        //when
        when(webClientBuilder.filter(any())).thenReturn(webClientBuilder);
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(authenticationServiceUri)).thenReturn(requestHeadersSpec);
        when(requestHeadersUriSpec.uri(companyServiceUri)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(UserDto.class)).thenReturn(Mono.just(userDto));
        when(responseSpec.bodyToMono(CompanyDto.class)).thenReturn(Mono.just(companyDto));
        when(docxReader.generateDocument(anyMap(), eq(FINANCIAL_STATEMENT_FILE_NAME))).thenThrow(DocxEvidenceReaderException.class);

        final var expectedException =
                catchException(() -> evidenceService.createFinancialStatement(financialStatementDto, sampleJwtToken));

        //then
        verify(docxReader, times(1)).generateDocument(anyMap(), eq(FINANCIAL_STATEMENT_FILE_NAME));
        verify(evidenceRepository, never()).save(any(Evidence.class));
        assertThat(expectedException)
                .isInstanceOf(DocxEvidenceReaderException.class);
    }

    @Test
    @DisplayName("Should return evidence detail dto if evidence id is provide")
    void test_07() {
        //when
        when(evidenceRepository.findByEvidenceId(evidenceIdNo1)).thenReturn(evidenceEntity);
        when(evidenceMapper.mapToDetailDto(evidenceEntity)).thenReturn(evidenceDetailsDto);

        final var result = evidenceService.getEvidenceDetailsById(evidenceIdNo1);

        //then
        assertThat(result)
                .isEqualTo(evidenceDetailsDto);
    }

    @Test
    @DisplayName("Should throw an exception if evidence entity not found using provided evidence id")
    void test_08() {
        //when
        when(evidenceRepository.findByEvidenceId(evidenceIdNo1)).thenReturn(null);

        final var expectedException = catchException(() -> evidenceService.getEvidenceDetailsById(evidenceIdNo1));

        //then
        assertThat(expectedException)
                .isInstanceOf(EvidenceNotFoundException.class)
                .hasMessageContaining("Evidence with provided id not exist");
    }

    @Test
    @DisplayName("Should return company evidences list sorted by creation date if company id is provided")
    void test_09() {
        //given
        final var evidenceNo1 = createEvidenceEntity(evidenceIdNo1, 2025, 12, 12, 13, 20);
        final var evidenceNo2 = createEvidenceEntity(evidenceIdNo2, 2024, 5, 5, 13, 50);
        final var evidenceNo3 = createEvidenceEntity(evidenceIdNo3, 2025, 2, 1, 15, 15);

        //when
        when(evidenceRepository.findAllByCompanyId(companyIdNo1)).thenReturn(List.of(evidenceNo1, evidenceNo2, evidenceNo3));
        when(evidenceMapper.mapToDto(evidenceNo1)).thenReturn(evidenceDtoNo1);
        when(evidenceMapper.mapToDto(evidenceNo2)).thenReturn(evidenceDtoNo2);
        when(evidenceMapper.mapToDto(evidenceNo3)).thenReturn(evidenceDtoNo3);

        final var result = evidenceService.getAllCompanyEvidences(companyIdNo1);

        //then
        assertThat(result)
                .containsExactly(evidenceDtoNo1, evidenceDtoNo3, evidenceDtoNo2);
    }

    @Test
    @DisplayName("Should return empty list if company has no evidence")
    void test_10() {
        //when
        when(evidenceRepository.findAllByCompanyId(companyIdNo1)).thenReturn(Collections.emptyList());

        final var result = evidenceService.getAllCompanyEvidences(companyIdNo1);

        //then
        assertThat(result)
                .isEmpty();
    }
}