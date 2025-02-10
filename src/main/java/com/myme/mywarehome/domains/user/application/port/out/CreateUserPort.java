package com.myme.mywarehome.domains.user.application.port.out;

import com.myme.mywarehome.domains.user.application.domain.User;

public interface CreateUserPort {
    User create(User user);
}
