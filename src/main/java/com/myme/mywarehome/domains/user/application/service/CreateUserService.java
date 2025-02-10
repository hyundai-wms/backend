package com.myme.mywarehome.domains.user.application.service;

import com.myme.mywarehome.domains.user.application.domain.User;
import com.myme.mywarehome.domains.user.application.port.in.CreateUserUseCase;
import com.myme.mywarehome.domains.user.application.port.out.CreateUserPort;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.myme.mywarehome.infrastructure.util.helper.StringHelper.generateRandomString;

@Service
@RequiredArgsConstructor
public class CreateUserService implements CreateUserUseCase {
    private final CreateUserPort createUserPort;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User create(User user) {

        String temporalPassword = generateRandomString(6);
        String encodedPassword = passwordEncoder.encode(temporalPassword);

        // todo : 만약 새로운 유저를 생성할 때 ADMIN role을 준다면, 해당 유저의 권한도 변경되어야 함..

        // 암호화된 비밀번호 저장
        user.changePassword(encodedPassword);
        User createdUser = createUserPort.create(user);

        // 생성된 유저에 임시 비밀번호를 설정하여 전달
        createdUser.changePassword(temporalPassword);

        return createdUser;
    }
}
