package com.myme.mywarehome.domains.notification.application.port.in.command;

import com.myme.mywarehome.domains.user.application.domain.Role;

public record NotificationCommand(
        String type,
        String code,
        String title,
        String message,
        Boolean isRead,
        Role role
        ) {
}
