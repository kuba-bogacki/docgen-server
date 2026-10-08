package com.document.queue;

import com.document.model.dto.QueueMessage;

public interface MessageQueuePublisher {

    void publishEvidenceDrafted(QueueMessage queueMessage);
}
