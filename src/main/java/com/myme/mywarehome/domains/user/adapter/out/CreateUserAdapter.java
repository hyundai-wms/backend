package com.myme.mywarehome.domains.user.adapter.out;

import com.myme.mywarehome.domains.user.adapter.out.persistence.UserJpaRepository;
import com.myme.mywarehome.domains.user.application.domain.User;
import com.myme.mywarehome.domains.user.application.port.out.CreateUserPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateUserAdapter implements CreateUserPort {
    private final UserJpaRepository userJpaRepository;

    @Override
    public User create(User user) {
        return userJpaRepository.save(user);
    }
}
