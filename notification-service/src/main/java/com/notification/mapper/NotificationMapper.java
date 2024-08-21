package com.notification.mapper;

import com.notification.model.Notification;
import com.notification.model.dto.NotificationDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    Notification toNotificationEntity(NotificationDto notificationDto);
    NotificationDto toNotificationDto(Notification notificationEntity);
}
