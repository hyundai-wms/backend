package com.myme.mywarehome.domains.user.application.service;

import com.myme.mywarehome.domains.user.application.domain.Role;
import com.myme.mywarehome.domains.user.application.domain.User;
import com.myme.mywarehome.domains.user.application.dto.in.UpdateUserRoleCommand;
import com.myme.mywarehome.domains.user.application.exception.UserNotFoundException;
import com.myme.mywarehome.domains.user.application.exception.UserRoleUpdateFailedException;
import com.myme.mywarehome.domains.user.application.port.in.UpdateUserRoleUseCase;
import com.myme.mywarehome.domains.user.application.port.out.GetUserPort;
import com.myme.mywarehome.domains.user.application.port.out.UpdateUserPort;
import com.myme.mywarehome.infrastructure.util.security.SecurityUtil;
import com.myme.mywarehome.infrastructure.util.security.session.SessionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateUserRoleService implements UpdateUserRoleUseCase {
    private final GetUserPort getUserPort;
    private final UpdateUserPort updateUserPort;
    private final SessionService sessionService;

    @Override
    @Transactional
    public User updateRole(UpdateUserRoleCommand command) {
        User user = getUserPort.findUserByUserId(command.userId())
                .orElseThrow(UserNotFoundException::new);

        // 역할을 바꾸려는 대상이 총관리자라면(본인이라면), 예외처리
        if(user.getRole().equals(Role.ROLE_ADMIN)){
            throw new UserRoleUpdateFailedException();
        }

        // 총관리자 역할을 주었다면 본인의 역할 강등
        if(command.role().equals(Role.ROLE_ADMIN)) {
            Long userId = SecurityUtil.getCurrentUserId();
            updateUserPort.updateUserRoleToMiddleManager(userId);
            // 세션 업데이트
            sessionService.updateUserSessionRole(
                    userId,
                    Role.ROLE_MIDDLE_MANAGER
            );
        }

        // 유저 역할 변경
        user.changeRole(command.role());
        User updatedUser = updateUserPort.updateUser(user)
                .orElseThrow(UserNotFoundException::new);

        // 세션 업데이트
        sessionService.updateUserSessionRole(
                command.userId(),
                command.role()
        );

        return updatedUser;
    }
}
