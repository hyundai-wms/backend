package com.myme.mywarehome.domains.user.application.port.out;

import com.myme.mywarehome.domains.user.application.domain.User;

import java.util.Optional;

public interface UpdateUserPort {
    Optional<User> findUserByUserId(Long userId);
    void updateUserPassword(User user);
}
