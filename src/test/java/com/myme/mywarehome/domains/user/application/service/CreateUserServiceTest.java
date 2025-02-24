package com.myme.mywarehome.domains.user.application.service;

import com.myme.mywarehome.domains.user.application.domain.Role;
import com.myme.mywarehome.domains.user.application.domain.User;
import com.myme.mywarehome.domains.user.application.dto.out.CreatedUserInfoResult;
import com.myme.mywarehome.domains.user.application.exception.UserDuplicateException;
import com.myme.mywarehome.domains.user.application.port.out.CreateUserPort;
import com.myme.mywarehome.domains.user.application.port.out.GetUserPort;
import com.myme.mywarehome.domains.user.application.port.out.UpdateUserPort;
import com.myme.mywarehome.infrastructure.util.security.SecurityUtil;
import com.myme.mywarehome.infrastructure.util.security.session.SessionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CreateUserServiceTest {

    @Mock
    private CreateUserPort createUserPort;
    @Mock
    private GetUserPort getUserPort;
    @Mock
    private UpdateUserPort updateUserPort;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private SessionService sessionService;

    private CreateUserService createUserService;
    private User testUser;

    @BeforeEach
    void setUp() {
        createUserService = new CreateUserService(
                createUserPort,
                getUserPort,
                updateUserPort,
                passwordEncoder,
                sessionService
        );

        testUser = User.builder()
                .id("testId")
                .phoneNumber("01012345678")
                .name("테스트")
                .role(Role.ROLE_WMS_MANAGER)
                .build();
    }

    @Test
    @DisplayName("새로운 일반 사용자를 생성한다")
    void create_withValidUser_returnsCreatedUserInfo() {
        // given
        given(getUserPort.existsUserById(any())).willReturn(false);
        given(getUserPort.existsUserByPhoneNumber(any())).willReturn(false);
        given(passwordEncoder.encode(any())).willReturn("encodedPassword");
        given(createUserPort.create(any())).willReturn(testUser);

        // when
        CreatedUserInfoResult result = createUserService.create(testUser);

        // then
        assertThat(result.user()).isEqualTo(testUser);
        assertThat(result.temporalPassword()).isNotNull();
        verify(createUserPort).create(any(User.class));
    }

    @Test
    @DisplayName("중복된 아이디로 가입 시도시 예외가 발생한다")
    void create_withDuplicatedId_throwsUserDuplicateException() {
        // given
        given(getUserPort.existsUserById(any())).willReturn(true);
        given(getUserPort.existsUserByPhoneNumber(any())).willReturn(false);

        // when & then
        assertThatThrownBy(() -> createUserService.create(testUser))
                .isInstanceOf(UserDuplicateException.class);
    }

    @Test
    @DisplayName("중복된 전화번호로 가입 시도시 예외가 발생한다")
    void create_withDuplicatedPhoneNumber_throwsUserDuplicateException() {
        // given
        given(getUserPort.existsUserById(any())).willReturn(false);
        given(getUserPort.existsUserByPhoneNumber(any())).willReturn(true);

        // when & then
        assertThatThrownBy(() -> createUserService.create(testUser))
                .isInstanceOf(UserDuplicateException.class);
    }

    @Test
    @DisplayName("관리자 권한으로 새로운 사용자를 생성하면 현재 사용자의 권한이 변경된다")
    void create_withAdminRole_updatesCurrentUserRole() {
        // given
        User adminUser = User.builder()
                .id("adminId")
                .phoneNumber("01087654321")
                .name("관리자")
                .role(Role.ROLE_ADMIN)
                .build();

        try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
            securityUtil.when(SecurityUtil::getCurrentUserId).thenReturn(1L);

            given(getUserPort.existsUserById(any())).willReturn(false);
            given(getUserPort.existsUserByPhoneNumber(any())).willReturn(false);
            given(passwordEncoder.encode(any())).willReturn("encodedPassword");
            given(createUserPort.create(any())).willReturn(adminUser);

            // when
            createUserService.create(adminUser);

            // then
            verify(updateUserPort).updateUserRoleToMiddleManager(1L);
            verify(sessionService).updateUserSessionRole(1L, Role.ROLE_MIDDLE_MANAGER);
        }
    }
}