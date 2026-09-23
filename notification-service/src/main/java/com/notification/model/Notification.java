package com.notification.model;

import com.notification.model.type.NotificationType;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Getter
@Setter
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Notification other)) return false;
        return this.notificationId != null && this.notificationId.equals(other.getNotificationId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
