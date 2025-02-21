package com.myme.mywarehome.domains.notification.application.port.out;

import com.myme.mywarehome.domains.notification.application.port.in.result.NotificationResult;

public interface UpdateNotificationPort {
    NotificationResult updateReadState(Long userNotificationId);
}
