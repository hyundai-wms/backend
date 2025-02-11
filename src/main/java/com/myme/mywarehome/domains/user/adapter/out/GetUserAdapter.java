package com.myme.mywarehome.domains.user.adapter.out;

import com.myme.mywarehome.domains.user.adapter.out.persistence.UserJpaRepository;
import com.myme.mywarehome.domains.user.application.domain.User;
import com.myme.mywarehome.domains.user.application.port.out.GetUserPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GetUserAdapter implements GetUserPort {
    private final UserJpaRepository userJpaRepository;

    @Override
    public Optional<User> findUserById(String id) {
        return userJpaRepository.findById(id);
    }

    @Override
    public Optional<User> findUserByUserId(Long userId) {
        return userJpaRepository.findByUserId(userId);
    }

    @Override
    public boolean existsUserById(String id) {
        return userJpaRepository.existsById(id);
    }

    @Override
    public boolean existsUserByPhoneNumber(String phoneNumber) {
        return userJpaRepository.existsByPhoneNumber(phoneNumber);
    }
}
