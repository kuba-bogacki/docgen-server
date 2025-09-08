package com.document.service;

import com.document.config.docx.DocxReaderConfiguration;
import com.document.exception.EvidenceNotFoundException;
import com.document.mapper.EvidenceMapper;
import com.document.model.Evidence;
import com.document.repository.EvidenceRepository;
import com.document.service.implementation.EvidenceServiceImplementation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EvidenceServiceImplementationTest extends EvidenceSamples {

    @Mock private EvidenceRepository evidenceRepository;
    @Mock private EvidenceMapper evidenceMapper;
    @Mock private DocxReaderConfiguration docxReader;
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
}