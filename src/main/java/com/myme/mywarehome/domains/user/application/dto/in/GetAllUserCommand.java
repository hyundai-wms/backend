package com.myme.mywarehome.domains.user.application.dto.in;

import com.myme.mywarehome.domains.user.application.domain.Role;
import lombok.Builder;

@Builder
public record GetAllUserCommand(
        String name,
        String id,
        String phoneNumber,
        Role role
) {
}
