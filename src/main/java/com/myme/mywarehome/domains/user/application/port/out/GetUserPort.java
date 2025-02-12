package com.myme.mywarehome.domains.user.application.port.out;

import com.myme.mywarehome.domains.user.application.domain.Role;
import com.myme.mywarehome.domains.user.application.domain.User;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GetUserPort {
    Optional<User> findUserByUserId(Long userId);
    Optional<User> findUserById(String id);
    Page<User> findAllUsers(String name, String id, String phoneNumber, Role role, Pageable pageable);
    boolean existsUserById(String id);
    boolean existsUserByPhoneNumber(String phoneNumber);
}
