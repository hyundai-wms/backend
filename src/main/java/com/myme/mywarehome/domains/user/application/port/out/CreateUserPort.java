package com.myme.mywarehome.domains.user.application.port.out;

import com.myme.mywarehome.domains.user.application.domain.User;

import java.util.Optional;

public interface CreateUserPort {
    boolean existsUserById(String id);
    boolean existsUserByPhoneNumber(String phoneNumber);
    void updateUserRoleToMiddleManager(Long userId);
    User create(User user);
}
