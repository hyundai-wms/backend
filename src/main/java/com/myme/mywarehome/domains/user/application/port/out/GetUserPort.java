package com.myme.mywarehome.domains.user.application.port.out;

import com.myme.mywarehome.domains.user.application.domain.User;

import java.util.Optional;

public interface GetUserPort {
    Optional<User> findUserByUserId(Long userId);
    Optional<User> findUserById(String id);
    boolean existsUserById(String id);
    boolean existsUserByPhoneNumber(String phoneNumber);
}
