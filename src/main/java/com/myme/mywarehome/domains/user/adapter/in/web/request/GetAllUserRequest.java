package com.myme.mywarehome.domains.user.adapter.in.web.request;

import com.myme.mywarehome.domains.user.application.domain.Role;
import com.myme.mywarehome.domains.user.application.dto.in.GetAllUserCommand;
import jakarta.validation.constraints.Pattern;

public record GetAllUserRequest(
        String name,
        String id,
        String phoneNumber,
        @Pattern(regexp = "^(ADMIN|MIDDLE_MANAGER|WMS_MANAGER|WORKER)$", message = "올바른 역할이 아닙니다.(ADMIN, MIDDLE_MANAGER, WMS_MANAGER, WORKER)")
        String role
) {
    public GetAllUserCommand toCommand() {
        GetAllUserCommand command;

        if(role != null && !role.isEmpty()) {
            command = GetAllUserCommand.builder()
                    .name(name)
                    .id(id)
                    .phoneNumber(phoneNumber)
                    .role(Role.fromString(role))
                    .build();
        } else {
            command = GetAllUserCommand.builder()
                    .name(name)
                    .id(id)
                    .phoneNumber(phoneNumber)
                    .build();
        }

        return command;
    }
}
