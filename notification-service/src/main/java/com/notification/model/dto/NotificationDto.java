package com.notification.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDto {

    private String notificationId;
    private String notificationRequesterId;
    private String notificationReceiverId;
    private String notificationMessage;
    private String notificationType;
}
