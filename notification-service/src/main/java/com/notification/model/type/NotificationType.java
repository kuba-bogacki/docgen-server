package com.notification.model.type;

public enum NotificationType {
    MEMBERSHIP_REQUEST,
    MEMBERSHIP_RESPONSE,
    EVENT_REQUEST,
    EVENT_RESPONSE;

    public static NotificationType fromString(String type) {
        for (NotificationType notificationType : NotificationType.values()) {
            if (notificationType.toString().equals(type)) {
                return notificationType;
            }
        }
        throw new IllegalArgumentException("Invalid notification type: " + type);
    }
}
