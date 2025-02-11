package com.myme.mywarehome.domains.user.application.port.in;

import com.myme.mywarehome.domains.user.application.domain.User;
import com.myme.mywarehome.domains.user.application.dto.out.CreatedUserInfoResult;

public interface CreateUserUseCase {
    CreatedUserInfoResult create(User user);
}
