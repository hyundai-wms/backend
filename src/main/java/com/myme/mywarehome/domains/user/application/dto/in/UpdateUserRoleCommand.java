package com.myme.mywarehome.domains.user.application.dto.in;

import com.myme.mywarehome.domains.user.application.domain.Role;

public record UpdateUserRoleCommand(
        Long userId,
        Role role
) {
    public static UpdateUserRoleCommand of(Long userId, String role) {
        return new UpdateUserRoleCommand(userId, Role.fromString(role));
    }
}
