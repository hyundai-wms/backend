package com.myme.mywarehome.domains.user.application.dto.out;

import com.myme.mywarehome.domains.user.application.domain.User;

public record CreatedUserInfoResult(
        User user,
        String temporalPassword
) {
}
