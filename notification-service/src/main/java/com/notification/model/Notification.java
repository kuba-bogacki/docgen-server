package com.notification.model;

import com.notification.model.type.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Document(value = "notification")
public class Notification {

    @MongoId(targetType = FieldType.OBJECT_ID)
    private String notificationId;
    private String notificationCompanyId;
    private String notificationUserPrincipal;
    private String notificationRequesterId;
    private String notificationReceiverId;
    private String notificationMessage;
    private NotificationType notificationType;
}
