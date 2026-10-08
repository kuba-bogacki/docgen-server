package com.document.queue.implemetation;

import com.document.client.docx.DocxReaderClient;
import com.document.exception.DocxEvidenceReaderException;
import com.document.exception.EvidenceNotFoundException;
import com.document.infrastructure.HttpClient;
import com.document.model.Evidence;
import com.document.model.dto.DocumentDto;
import com.document.model.dto.EvidenceNotificationDto;
import com.document.model.dto.QueueMessage;
import com.document.model.type.EvidenceStatus;
import com.document.queue.MessageQueueSubscriber;
import com.document.repository.EvidenceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.document.util.ApplicationConstants.EVIDENCE_QUEUE;

@Slf4j
@Component
@RequiredArgsConstructor
public class DefaultMessageQueueSubscriber implements MessageQueueSubscriber {

    private final HttpClient httpClient;
    private final DocxReaderClient docxReader;
    private final EvidenceRepository evidenceRepository;

    @Override
    @JmsListener(destination = EVIDENCE_QUEUE)
    public void subscribeEvidenceDrafted(QueueMessage queueMessage) {
        log.info("Event 'Evidence drafted' with id: {} successfully subscribed.", queueMessage.getMessageId());
        final Evidence evidenceEntity = getEvidenceEntity(queueMessage.getEvidenceId());
        final StringBuilder notification = new StringBuilder()
                .append("New evidence ");
        try {
            final DocumentDto document = docxReader.generateDocument(queueMessage.getPlaceholders(), queueMessage.getTemplateFileName());

            evidenceEntity.setEvidenceContent(document.getContent());
            evidenceEntity.setEvidenceStatus(EvidenceStatus.CREATED);

            notification.append("has been successfully created.");

            final EvidenceNotificationDto evidenceNotificationDto = EvidenceNotificationDto.builder()
                    .notificationMessage(notification.toString())
                    .notificationType("EVIDENCE_CREATED")
                    .build();

            httpClient.createCurrentUserNotification(queueMessage.getUserEmail(), evidenceNotificationDto);
            log.info("Evidence with id {} has been successfully created.", evidenceEntity.getEvidenceId());
        } catch (DocxEvidenceReaderException exception) {
            evidenceEntity.setEvidenceStatus(EvidenceStatus.FAILED);

            notification.append("creation has been failed.");

            final EvidenceNotificationDto evidenceNotificationDto = EvidenceNotificationDto.builder()
                    .notificationMessage(notification.toString())
                    .notificationType("EVIDENCE_FAILED")
                    .build();

            httpClient.createCurrentUserNotification(queueMessage.getUserEmail(), evidenceNotificationDto);
            log.error("Evidence creation with id {} has been failed. Cause: {}.", evidenceEntity.getEvidenceId(), exception.getMessage());
        } finally {
            final Evidence updatedEntity = evidenceRepository.save(evidenceEntity);
            log.info("Evidence with status '{}' and with id {} has been updated.", updatedEntity.getEvidenceStatus(), updatedEntity.getEvidenceId());
        }
    }

    private Evidence getEvidenceEntity(String evidenceId) {
        final Optional<Evidence> evidence = Optional.ofNullable(evidenceRepository.findByEvidenceId(evidenceId));
        if (evidence.isEmpty()) {
            throw new EvidenceNotFoundException(String.format("Evidence with provided id [%s] not exist", evidenceId));
        }
        return evidence.get();
    }
}
