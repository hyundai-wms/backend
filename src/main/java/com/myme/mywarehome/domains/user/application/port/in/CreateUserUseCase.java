package com.myme.mywarehome.domains.user.application.port.in;

import com.myme.mywarehome.domains.user.application.domain.User;
import com.myme.mywarehome.domains.user.application.dto.out.CreatedUserInfoDto;

public interface CreateUserUseCase {
    CreatedUserInfoDto create(User user);
}
