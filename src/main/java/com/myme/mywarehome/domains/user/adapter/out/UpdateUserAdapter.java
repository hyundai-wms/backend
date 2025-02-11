package com.myme.mywarehome.domains.user.adapter.out;

import com.myme.mywarehome.domains.user.adapter.out.persistence.UserJpaRepository;
import com.myme.mywarehome.domains.user.application.domain.User;
import com.myme.mywarehome.domains.user.application.port.out.UpdateUserPort;
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
    public void updateUserPassword(User user) {
        if(user.getUserId() != null){
            userJpaRepository.save(user);
        } else {
            log.error("Password Not Changed : User not found when changing password");
        }
    }
}
