package com.myme.mywarehome.domains.notification.adapter.out.event;

import com.myme.mywarehome.domains.notification.application.port.in.result.NotificationResult;
import com.myme.mywarehome.domains.user.application.domain.Role;
import java.util.Set;

public record UserNotificationEvent(
        Set<Role> targetRoles,
        NotificationResult data
) {

}
