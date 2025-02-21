package com.myme.mywarehome.domains.user.adapter.out;

import com.myme.mywarehome.domains.user.adapter.out.persistence.UserJpaRepository;
import com.myme.mywarehome.domains.user.application.domain.Role;
import com.myme.mywarehome.domains.user.application.domain.User;
import com.myme.mywarehome.domains.user.application.port.out.GetUserPort;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public Page<User> findAllUsers(String name, String id, String phoneNumber, Role role,
            Pageable pageable) {
        return userJpaRepository.findByConditions(name, id, phoneNumber, role, pageable);
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

    @Override
    public List<User> findAllUserByRole(Role role) {
        return userJpaRepository.findAllByRole(role);
    }
}
