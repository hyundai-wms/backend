package com.myme.mywarehome.domains.user.adapter.in.web.response;

import com.myme.mywarehome.domains.user.application.domain.User;

import java.time.LocalDateTime;

public record UserInfoResponse(
        Long userId,
        String name,
        String phoneNumber,
        String id,
        String role,
        Boolean isInitLogin,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static UserInfoResponse from(User user) {
        return new UserInfoResponse(
                user.getUserId(),
                user.getName(),
                user.getPhoneNumber(),
                user.getId(),
                user.getRole().getRoleName(),
                user.getIsInitLogin(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
