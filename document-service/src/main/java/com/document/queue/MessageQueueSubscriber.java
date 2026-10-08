package com.document.queue;

import com.document.model.dto.QueueMessage;

public interface MessageQueueSubscriber {

    void subscribeEvidenceDrafted(QueueMessage queueMessage);
}
