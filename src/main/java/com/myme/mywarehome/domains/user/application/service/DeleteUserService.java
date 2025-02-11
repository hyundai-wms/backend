package com.myme.mywarehome.domains.user.application.service;

import com.myme.mywarehome.domains.user.application.domain.User;
import com.myme.mywarehome.domains.user.application.exception.UserNotFoundException;
import com.myme.mywarehome.domains.user.application.port.in.DeleteUserUseCase;
import com.myme.mywarehome.domains.user.application.port.out.DeleteUserPort;
import com.myme.mywarehome.domains.user.application.port.out.GetUserPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteUserService implements DeleteUserUseCase {
    private final GetUserPort getUserPort;
    private final DeleteUserPort deleteUserPort;

    @Override
    public void deleteUser(Long userId) {
        User user = getUserPort.findUserByUserId(userId)
                .orElseThrow(UserNotFoundException::new);
        // 본인(총관리자)은 삭제되면 안됨.

        // 삭제 시 세션 정보도 함께 날아가야 함.
    }
}
