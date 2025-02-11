package com.myme.mywarehome.domains.user.application.port.out;

import com.myme.mywarehome.domains.user.application.domain.User;

import java.util.Optional;

public interface UpdateUserPort {
    void updateUserRoleToMiddleManager(Long userId);
    Optional<User> updateUser(User user);
}
