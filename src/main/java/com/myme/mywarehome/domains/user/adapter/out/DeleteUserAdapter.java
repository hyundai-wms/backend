package com.myme.mywarehome.domains.user.adapter.out;

import com.myme.mywarehome.domains.user.adapter.out.persistence.UserJpaRepository;
import com.myme.mywarehome.domains.user.application.port.out.DeleteUserPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeleteUserAdapter implements DeleteUserPort {
    private final UserJpaRepository userJpaRepository;

    @Override
    public void delete(Long userId) {
        userJpaRepository.deleteById(userId);
    }
}
