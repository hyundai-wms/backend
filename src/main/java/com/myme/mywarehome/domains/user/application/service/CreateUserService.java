package com.myme.mywarehome.domains.user.application.service;

import com.myme.mywarehome.domains.user.adapter.in.web.request.CreateUserRequest;
import com.myme.mywarehome.domains.user.adapter.in.web.response.CreateUserResponse;
import com.myme.mywarehome.domains.user.application.domain.Role;
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
    @Transactional
    public CreateUserResponse create(CreateUserRequest createUserRequest) {

        String temporalPassword = generateRandomString(6);
        String encodedPassword = passwordEncoder.encode(temporalPassword);

        // todo : 만약 새로운 유저를 생성할 때 ADMIN role을 준다면, 해당 유저의 권한도 변경되어야 함..

        User user = User.builder()
                .name(createUserRequest.name())
                .phoneNumber(createUserRequest.phoneNumber())
                .id(createUserRequest.id())
                .password(encodedPassword)
                .role(Role.fromString(createUserRequest.role()))
                .build();

        User savedUser = createUserPort.create(user);

        return CreateUserResponse.of(savedUser, temporalPassword);
    }
}
