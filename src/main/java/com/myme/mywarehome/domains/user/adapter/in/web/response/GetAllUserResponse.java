package com.myme.mywarehome.domains.user.adapter.in.web.response;

import com.myme.mywarehome.domains.user.application.domain.User;
import java.util.List;
import org.springframework.data.domain.Page;

public record GetAllUserResponse(
        List<UserInfoResponse> content,
        Integer pageNumber,
        Integer pageSize,
        Long totalElements,
        Integer totalPages,
        Boolean isFirst,
        Boolean isLast
) {
    public static GetAllUserResponse of(Page<User> userList) {
        return new GetAllUserResponse(
                userList.getContent().stream()
                        .map(UserInfoResponse::of)
                        .toList(),
                userList.getNumber(),
                userList.getSize(),
                userList.getTotalElements(),
                userList.getTotalPages(),
                userList.isFirst(),
                userList.isLast()
        );
    }
}
