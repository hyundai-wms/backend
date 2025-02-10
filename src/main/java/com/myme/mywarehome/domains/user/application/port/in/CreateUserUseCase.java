package com.myme.mywarehome.domains.user.application.port.in;

import com.myme.mywarehome.domains.user.application.domain.User;

public interface CreateUserUseCase {
    User create(User user);
}
