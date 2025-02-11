package com.myme.mywarehome.domains.user.application.port.in;

import com.myme.mywarehome.domains.user.application.domain.User;

public interface LoginUseCase {
    User login(String id, String password);
}
