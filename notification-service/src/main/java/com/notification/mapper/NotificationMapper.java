package com.notification.mapper;

import com.notification.model.Notification;
import com.notification.model.dto.NotificationDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    Notification toEntity(NotificationDto notificationDto);
}
