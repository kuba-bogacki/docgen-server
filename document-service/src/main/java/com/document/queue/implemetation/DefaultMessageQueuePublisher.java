package com.document.queue.implemetation;

import com.document.model.dto.QueueMessage;
import com.document.queue.MessageQueuePublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsClient;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

import static com.document.util.ApplicationConstants.EVIDENCE_QUEUE;

@Slf4j
@Component
@RequiredArgsConstructor
public class DefaultMessageQueuePublisher implements MessageQueuePublisher {

    private final JmsClient jmsClient;

    @Override
    public void publishEvidenceDrafted(QueueMessage queueMessage) {
        final QueueMessage message = queueMessage.toBuilder()
                .messageId(UUID.randomUUID())
                .occurredAt(Instant.now())
                .build();

        jmsClient.destination(EVIDENCE_QUEUE).send(message);
        log.info("Message 'Evidence drafted' with id: {} successfully published.", message.getMessageId());
    }
}
