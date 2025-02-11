package com.myme.mywarehome.domains.user.adapter.out;

import com.myme.mywarehome.domains.user.adapter.out.persistence.UserJpaRepository;
import com.myme.mywarehome.domains.user.application.domain.Role;
import com.myme.mywarehome.domains.user.application.domain.User;
import com.myme.mywarehome.domains.user.application.exception.UserNotFoundException;
import com.myme.mywarehome.domains.user.application.port.out.UpdateUserPort;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class UpdateUserAdapter implements UpdateUserPort {
    private final UserJpaRepository userJpaRepository;

    @Override
    public Optional<User> findUserByUserId(Long userId) {
        return userJpaRepository.findByUserId(userId);
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
    public Optional<User> updateUser(User user) {
        if(user.getUserId() != null){
            return Optional.of(userJpaRepository.save(user));
        } else {
            log.error("User Not Changed : User not found when changing user");
            return Optional.empty();
        }
    }
}
