package com.myme.mywarehome.domains.user.application.service;

import com.myme.mywarehome.domains.user.adapter.out.exception.InvalidCurrentPasswordException;
import com.myme.mywarehome.domains.user.application.domain.User;
import com.myme.mywarehome.domains.user.application.exception.UserNotFoundException;
import com.myme.mywarehome.domains.user.application.port.in.UpdateUserPasswordUseCase;
import com.myme.mywarehome.domains.user.application.port.out.GetUserPort;
import com.myme.mywarehome.domains.user.application.port.out.UpdateUserPort;
import com.myme.mywarehome.infrastructure.util.security.SecurityUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateUserPasswordService implements UpdateUserPasswordUseCase {
    private final GetUserPort getUserPort;
    private final UpdateUserPort updateUserPort;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void updatePassword(String oldPassword, String newPassword) {
        Long userId = SecurityUtil.getCurrentUserId();

        User user = getUserPort.findUserByUserId(userId)
                .orElseThrow(UserNotFoundException::new);

        // 기존 비밀번호 유효성 검사
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new InvalidCurrentPasswordException();
        }

        // 새로운 비밀번호 저장
        String encodedNewPassword = passwordEncoder.encode(newPassword);
        user.changePassword(encodedNewPassword);
        updateUserPort.updateUser(user);
    }
}
