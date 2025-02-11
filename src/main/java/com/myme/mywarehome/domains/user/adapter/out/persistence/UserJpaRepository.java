package com.myme.mywarehome.domains.user.adapter.out.persistence;

import com.myme.mywarehome.domains.user.application.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserId(Long userId);
    Optional<User> findById(String id);

    boolean existsById(String id);
    boolean existsByPhoneNumber(String phoneNumber);
}
