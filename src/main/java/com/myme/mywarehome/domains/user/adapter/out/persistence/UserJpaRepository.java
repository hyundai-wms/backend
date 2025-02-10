package com.myme.mywarehome.domains.user.adapter.out.persistence;

import com.myme.mywarehome.domains.user.application.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<User, Long> {
}
