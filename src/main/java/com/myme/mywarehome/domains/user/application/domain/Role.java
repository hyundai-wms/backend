package com.myme.mywarehome.domains.user.application.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum Role {
    ROLE_ADMIN("ADMIN"),
    ROLE_MIDDLE_MANAGER("MIDDLE_MANAGER"),
    ROLE_WMS_MANAGER("WMS_MANAGER"),
    ROLE_WORKER("WORKER");

    private final String roleName;

    public static Role fromString(String role) {
        return Arrays.stream(Role.values())
                .filter(r -> r.getRoleName().equals(role))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid role: " + role));
    }
}
