package com.myme.mywarehome.domains.user.adapter.in.web.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record UpdateUserRoleRequest(
        @NotNull(message = "역할은 필수입니다.")
        @Pattern(regexp = "^(ADMIN|MIDDLE_MANAGER|WMS_MANAGER|WORKER)$", message = "올바른 역할이 아닙니다.(ADMIN, MIDDLE_MANAGER, WMS_MANAGER, WORKER)")
        String role
) {
}
