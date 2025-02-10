package com.myme.mywarehome.domains.user.adapter.in.web.response;

import com.myme.mywarehome.domains.user.application.domain.User;

import java.time.LocalDateTime;

public record CreateUserResponse(
        Long userId,
        String name,
        String phoneNumber,
        String id,
        String password,
        String role,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static CreateUserResponse of(User user, String temporalPassword) {
        return new CreateUserResponse(
                user.getUserId(),
                user.getName(),
                user.getPhoneNumber(),
                user.getId(),
                temporalPassword,
                user.getRole().getRoleName(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
