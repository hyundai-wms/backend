package com.myme.mywarehome.domains.notification.application.port.out;

import com.myme.mywarehome.domains.notification.application.domain.Notification;
import com.myme.mywarehome.domains.notification.application.domain.UserNotification;
import java.util.List;

public interface CreateNotificationPort {
    void create(Notification notification);
    List<UserNotification>  createAll(List<UserNotification> notifications);
}
