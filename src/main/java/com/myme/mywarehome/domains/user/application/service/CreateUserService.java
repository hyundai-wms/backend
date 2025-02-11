package com.myme.mywarehome.domains.user.application.service;

import com.myme.mywarehome.domains.user.application.domain.Role;
import com.myme.mywarehome.domains.user.application.domain.User;
import com.myme.mywarehome.domains.user.application.dto.out.CreatedUserInfoResult;
import com.myme.mywarehome.domains.user.application.exception.UserDuplicateException;
import com.myme.mywarehome.domains.user.application.port.in.CreateUserUseCase;
import com.myme.mywarehome.domains.user.application.port.out.CreateUserPort;
import com.myme.mywarehome.domains.user.application.port.out.GetUserPort;
import com.myme.mywarehome.domains.user.application.port.out.UpdateUserPort;
import com.myme.mywarehome.infrastructure.util.security.SecurityUtil;
import com.myme.mywarehome.infrastructure.util.security.session.SessionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.myme.mywarehome.infrastructure.util.helper.StringHelper.generateRandomString;

@Service
@RequiredArgsConstructor
public class CreateUserService implements CreateUserUseCase {
    private final CreateUserPort createUserPort;
    private final GetUserPort getUserPort;
    private final UpdateUserPort updateUserPort;
    private final PasswordEncoder passwordEncoder;
    private final SessionService sessionService;

    @Override
    @Transactional
    public CreatedUserInfoResult create(User user) {
        // ID, PhoneNumber 중복 여부 검사
        boolean isIdDuplicated = getUserPort.existsUserById(user.getId());
        boolean isPhoneNumberDuplicated = getUserPort.existsUserByPhoneNumber(user.getPhoneNumber());

        if(isIdDuplicated && isPhoneNumberDuplicated) {
            throw new UserDuplicateException(user.getId(), user.getPhoneNumber());
        } else if(isIdDuplicated) {
            throw new UserDuplicateException(user.getId(), null);
        } else if(isPhoneNumberDuplicated) {
            throw new UserDuplicateException(null, user.getPhoneNumber());
        }

        // 임시 비밀번호 생성
        String temporalPassword = generateRandomString(6);
        String encodedPassword = passwordEncoder.encode(temporalPassword);

        // 만약 새로운 유저에 ADMIN role을 부여한다면, 본인은 Middle Manager로 강등되어야 함.
        if(user.getRole().equals(Role.ROLE_ADMIN)) {
            Long userId = SecurityUtil.getCurrentUserId();
            updateUserPort.updateUserRoleToMiddleManager(userId);
            // 세션 업데이트
            sessionService.updateUserSessionRole(
                    userId,
                    Role.ROLE_MIDDLE_MANAGER
            );
        }

        // 암호화된 비밀번호 저장
        user.changePassword(encodedPassword);
        User createdUser = createUserPort.create(user);

        // 생성된 유저에 임시 비밀번호를 함께 전달
        return new CreatedUserInfoResult(createdUser, temporalPassword);
    }
}
