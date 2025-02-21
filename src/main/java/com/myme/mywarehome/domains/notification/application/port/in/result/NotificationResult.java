package com.myme.mywarehome.domains.notification.application.port.in.result;

import com.myme.mywarehome.domains.notification.application.domain.Notification;
import com.myme.mywarehome.domains.notification.application.domain.UserNotification;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record NotificationResult(
        Long userNotificationId,
        String type,
        String code,
        String title,
        String message,
        Boolean isRead,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
        ) {
    public static NotificationResult of(UserNotification userNotification) {
        return new NotificationResult(
                userNotification.getUserNotificationId(),
                userNotification.getNotification().getNotificationType(),
                userNotification.getNotification().getCode(),
                userNotification.getNotification().getTitle(),
                userNotification.getNotification().getMessage(),
                userNotification.getIsRead(),
                userNotification.getNotification().getCreatedAt(),
                userNotification.getNotification().getUpdatedAt()
        );
    }
}
