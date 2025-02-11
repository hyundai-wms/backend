package com.myme.mywarehome.domains.user.adapter.in.web.response;

import com.myme.mywarehome.domains.user.application.dto.out.CreatedUserInfoResult;

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
    public static CreateUserResponse of(CreatedUserInfoResult createdUserInfoDto) {
        return new CreateUserResponse(
                createdUserInfoDto.user().getUserId(),
                createdUserInfoDto.user().getName(),
                createdUserInfoDto.user().getPhoneNumber(),
                createdUserInfoDto.user().getId(),
                createdUserInfoDto.temporalPassword(),
                createdUserInfoDto.user().getRole().getRoleName(),
                createdUserInfoDto.user().getCreatedAt(),
                createdUserInfoDto.user().getUpdatedAt()
        );
    }
}
