package com.myme.mywarehome.domains.user.adapter.out;

import com.myme.mywarehome.domains.user.adapter.out.persistence.UserJpaRepository;
import com.myme.mywarehome.domains.user.application.domain.Role;
import com.myme.mywarehome.domains.user.application.domain.User;
import com.myme.mywarehome.domains.user.application.exception.UserNotFoundException;
import com.myme.mywarehome.domains.user.application.port.out.CreateUserPort;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateUserAdapter implements CreateUserPort {
    private final UserJpaRepository userJpaRepository;

    @Override
    public boolean existsUserById(String id) {
        return userJpaRepository.existsById(id);
    }

    @Override
    public boolean existsUserByPhoneNumber(String phoneNumber) {
        return userJpaRepository.existsByPhoneNumber(phoneNumber);
    }

    @Override
    @Transactional
    public void updateUserRoleToMiddleManager(Long userId) {
        User user = userJpaRepository.findById(userId)
                        .orElseThrow(UserNotFoundException::new);
        user.changeRole(Role.ROLE_MIDDLE_MANAGER);
        userJpaRepository.save(user);
    }

    @Override
    public User create(User user) {
        return userJpaRepository.save(user);
    }
}
