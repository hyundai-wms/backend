package com.myme.mywarehome.domains.notification.application.port.in;

import com.myme.mywarehome.domains.notification.application.port.in.result.NotificationResult;

public interface UpdateNotificationUseCase {
    NotificationResult updateReadState(Long userNotificationId);
}
