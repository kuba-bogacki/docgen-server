package com.notification.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationRequest {

    private String notificationId;
    private String notificationCompanyId;
    private String notificationUserPrincipal;
    private String notificationRequesterId;
    private String notificationReceiverId;
    private String notificationMessage;
    private String notificationType;
}
