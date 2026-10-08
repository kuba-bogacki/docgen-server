package com.document.service.implementation;

import com.document.exception.CompanyNotFoundException;
import com.document.exception.EvidenceNotFoundException;
import com.document.exception.UserNotFoundException;
import com.document.infrastructure.HttpClient;
import com.document.mapper.EvidenceMapper;
import com.document.model.Evidence;
import com.document.model.dto.QueueMessage;
import com.document.queue.MessageQueuePublisher;
import com.document.repository.EvidenceRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.MessagingException;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static com.document.model.type.EvidenceType.FINANCIAL_STATEMENT;
import static com.document.util.ApplicationConstants.FINANCIAL_STATEMENT_FILE_NAME;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EvidenceServiceImplementationTest extends EvidenceSamples {

    @Mock private HttpClient httpClient;
    @Mock private EvidenceMapper evidenceMapper;
    @Mock private EvidenceRepository evidenceRepository;
    @Mock private MessageQueuePublisher messageQueuePublisher;
    @Captor private ArgumentCaptor<Evidence> evidenceCaptor;
    @Captor private ArgumentCaptor<QueueMessage> queueMessageCaptor;
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
    @DisplayName("Should publish message on queue if necessary data was provide")
    void test_03() {
        //when
        when(httpClient.getCurrentUserDto(userEmail)).thenReturn(userDto);
        when(httpClient.getCurrentCompanyDto(companyIdNo1, userEmail)).thenReturn(companyDto);
        when(evidenceRepository.save(any(Evidence.class))).thenReturn(evidenceEntity);
        doNothing().when(messageQueuePublisher).publishEvidenceDrafted(any(QueueMessage.class));

        evidenceService.createFinancialStatement(financialStatementDto, userEmail);

        //then
        verify(messageQueuePublisher, times(1)).publishEvidenceDrafted(queueMessageCaptor.capture());
        verify(evidenceRepository, times(1)).save(evidenceCaptor.capture());
        assertAll(
                () -> assertThat(queueMessageCaptor.getValue().getUserEmail())
                        .isEqualTo(userEmail),
                () -> assertThat(queueMessageCaptor.getValue().getTemplateFileName())
                        .isEqualTo(FINANCIAL_STATEMENT_FILE_NAME),
                () -> assertThat(evidenceCaptor.getValue().getEvidenceType())
                        .isEqualTo(FINANCIAL_STATEMENT),
                () -> assertThat(queueMessageCaptor.getValue().getPlaceholders())
                        .isInstanceOf(HashMap.class),
                () -> assertThat(evidenceCaptor.getValue().getEvidenceName())
                        .isEqualTo("Financial_statement_Allegro_sp._z_o.o.")
        );
    }

    @Test
    @DisplayName("Should throw an exception if could not get current user dto")
    void test_04() {
        //when
        when(httpClient.getCurrentUserDto(userEmail)).thenReturn(null);

        final var expectedException =
                catchException(() -> evidenceService.createFinancialStatement(financialStatementDto, userEmail));

        //then
        verify(messageQueuePublisher, never()).publishEvidenceDrafted(queueMessage);
        verify(evidenceRepository, never()).save(any(Evidence.class));
        assertThat(expectedException)
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("Couldn't find current user with provided id in database");
    }

    @Test
    @DisplayName("Should throw an exception if could not get current company dto")
    void test_05() {
        //when
        when(httpClient.getCurrentUserDto(userEmail)).thenReturn(userDto);
        when(httpClient.getCurrentCompanyDto(companyIdNo1, userEmail)).thenReturn(null);

        final var expectedException =
                catchException(() -> evidenceService.createFinancialStatement(financialStatementDto, userEmail));

        //then
        verify(messageQueuePublisher, never()).publishEvidenceDrafted(queueMessage);
        verify(evidenceRepository, never()).save(any(Evidence.class));
        assertThat(expectedException)
                .isInstanceOf(CompanyNotFoundException.class)
                .hasMessageContaining("Couldn't find company with provided id in database");
    }

    @Test
    @DisplayName("Should throw an exception if error occur due publishing message on queue")
    void test_06() {
        //when
        when(httpClient.getCurrentUserDto(userEmail)).thenReturn(userDto);
        when(httpClient.getCurrentCompanyDto(companyIdNo1, userEmail)).thenReturn(companyDto);
        when(evidenceRepository.save(any(Evidence.class))).thenReturn(evidenceEntity);
        doThrow(MessagingException.class).when(messageQueuePublisher).publishEvidenceDrafted(any(QueueMessage.class));

        final var expectedException =
                catchException(() -> evidenceService.createFinancialStatement(financialStatementDto, userEmail));

        //then
        verify(messageQueuePublisher, times(1)).publishEvidenceDrafted(any(QueueMessage.class));
        verify(evidenceRepository, times(1)).save(any(Evidence.class));
        assertThat(expectedException)
                .isInstanceOf(MessagingException.class);
    }

    @Test
    @DisplayName("Should return evidence detail dto if evidence id is provide")
    void test_07() {
        //when
        when(evidenceRepository.findByEvidenceId(evidenceIdNo1)).thenReturn(evidenceEntity);
        when(evidenceMapper.mapToDetailDto(evidenceEntity)).thenReturn(evidenceDetailsDto);

        final var result = evidenceService.getEvidenceDetailsById(evidenceIdNo1);

        //then
        verify(evidenceRepository, times(1)).findByEvidenceId(anyString());
        verify(evidenceMapper, times(1)).mapToDetailDto(any(Evidence.class));
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