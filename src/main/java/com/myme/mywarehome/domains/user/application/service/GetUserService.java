package com.myme.mywarehome.domains.user.application.service;

import com.myme.mywarehome.domains.user.application.domain.User;
import com.myme.mywarehome.domains.user.application.domain.exception.UserNotFoundException;
import com.myme.mywarehome.domains.user.application.port.in.GetUserUseCase;
import com.myme.mywarehome.domains.user.application.port.out.GetUserPort;
import com.myme.mywarehome.infrastructure.util.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetUserService implements GetUserUseCase {
    private final GetUserPort getUserPort;

    @Override
    public User getUser() {
        Long userId = SecurityUtil.getCurrentUserId();
        return getUserPort.findUserByUserId(userId)
                .orElseThrow(UserNotFoundException::new);
    }
}
