package com.myme.mywarehome.domains.user.adapter.out.persistence;

import com.myme.mywarehome.domains.user.application.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserJpaRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserId(Long userId);
    Optional<User> findById(String id);

    boolean existsById(String id);
    boolean existsByPhoneNumber(String phoneNumber);
}
