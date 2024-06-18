package com.notification.model;

import com.notification.model.type.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Document(value = "notification")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Notification {

    @MongoId(targetType = FieldType.STRING)
    private String notificationId;
    private String notificationRequesterId;
    private String notificationReceiverId;
    private String notificationMessage;
    private NotificationType notificationType;
}
