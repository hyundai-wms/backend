package com.myme.mywarehome.domains.notification.application.port.in.result;

import com.myme.mywarehome.domains.notification.application.domain.Notification;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record NotificationResult(
        Long notificationId,
        String type,
        String code,
        String title,
        String message,
        Boolean isRead,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
        ) {
    public static NotificationResult of(Notification notification, Boolean isRead) {
        return new NotificationResult(
                notification.getNotificationId(),
                notification.getNotificationType(),
                notification.getCode(),
                notification.getTitle(),
                notification.getMessage(),
                isRead,
                notification.getCreatedAt(),
                notification.getUpdatedAt()
        );
    }
}
