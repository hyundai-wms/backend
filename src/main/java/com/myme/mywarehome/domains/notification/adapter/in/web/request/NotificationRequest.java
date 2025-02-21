package com.myme.mywarehome.domains.notification.adapter.in.web.request;

import com.myme.mywarehome.domains.notification.application.port.in.command.NotificationCommand;
import com.myme.mywarehome.domains.user.application.domain.Role;
import jakarta.validation.constraints.Pattern;

public record NotificationRequest(
        String type,
        String code,
        String title,
        String message,
        Boolean isRead,
        @Pattern(regexp = "^(ADMIN|MIDDLE_MANAGER|WMS_MANAGER|WORKER)$", message = "올바른 역할이 아닙니다.(ADMIN, MIDDLE_MANAGER, WMS_MANAGER, WORKER)")
        String role
) {
    public NotificationCommand toCommand() {
        return new NotificationCommand(
                type,
                code,
                title,
                message,
                isRead,
                Role.fromString(role)
        );
    }
}
