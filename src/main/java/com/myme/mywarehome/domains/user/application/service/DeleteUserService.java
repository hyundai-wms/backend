package com.myme.mywarehome.domains.user.application.service;

import com.myme.mywarehome.domains.user.application.domain.Role;
import com.myme.mywarehome.domains.user.application.domain.User;
import com.myme.mywarehome.domains.user.application.exception.UserDeletionFailedException;
import com.myme.mywarehome.domains.user.application.exception.UserNotFoundException;
import com.myme.mywarehome.domains.user.application.port.in.DeleteUserUseCase;
import com.myme.mywarehome.domains.user.application.port.out.DeleteUserPort;
import com.myme.mywarehome.domains.user.application.port.out.GetUserPort;
import com.myme.mywarehome.infrastructure.util.security.session.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteUserService implements DeleteUserUseCase {
    private final GetUserPort getUserPort;
    private final DeleteUserPort deleteUserPort;
    private final SessionService sessionService;

    @Override
    public void deleteUser(Long userId) {
        User user = getUserPort.findUserByUserId(userId)
                .orElseThrow(UserNotFoundException::new);

        // 본인(총관리자)은 삭제되면 안됨.
        if (user.getRole().equals(Role.ROLE_ADMIN)) {
            throw new UserDeletionFailedException();
        }

        deleteUserPort.delete(userId);

        // 세션 업데이트
        sessionService.deleteUserSessions(userId);
    }
}
