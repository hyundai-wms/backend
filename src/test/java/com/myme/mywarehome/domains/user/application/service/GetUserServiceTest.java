package com.myme.mywarehome.domains.user.application.service;

import com.myme.mywarehome.domains.user.application.domain.Role;
import com.myme.mywarehome.domains.user.application.domain.User;
import com.myme.mywarehome.domains.user.application.exception.UserNotFoundException;
import com.myme.mywarehome.domains.user.application.port.out.GetUserPort;
import com.myme.mywarehome.infrastructure.util.security.SecurityUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class GetUserServiceTest {

    @Mock
    private GetUserPort getUserPort;

    private GetUserService getUserService;
    private User testUser;
    private List<User> middleManagers;

    @BeforeEach
    void setUp() {
        getUserService = new GetUserService(getUserPort);

        testUser = User.builder()
                .id("testId")
                .name("테스트")
                .phoneNumber("01012345678")
                .role(Role.ROLE_WMS_MANAGER)
                .build();

        middleManagers = List.of(
                User.builder()
                        .id("manager1")
                        .name("매니저1")
                        .phoneNumber("01011111111")
                        .role(Role.ROLE_MIDDLE_MANAGER)
                        .build(),
                User.builder()
                        .id("manager2")
                        .name("매니저2")
                        .phoneNumber("01022222222")
                        .role(Role.ROLE_MIDDLE_MANAGER)
                        .build()
        );
    }

    @Test
    @DisplayName("현재 로그인한 사용자의 정보를 조회한다")
    void getUser_withValidUserId_returnsUser() {
        // given
        Long userId = 1L;
        try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
            securityUtil.when(SecurityUtil::getCurrentUserId).thenReturn(userId);
            given(getUserPort.findUserByUserId(userId)).willReturn(Optional.of(testUser));

            // when
            User result = getUserService.getUser();

            // then
            assertThat(result).isEqualTo(testUser);
            verify(getUserPort).findUserByUserId(userId);
        }
    }

    @Test
    @DisplayName("존재하지 않는 사용자 조회시 예외가 발생한다")
    void getUser_withInvalidUserId_throwsUserNotFoundException() {
        // given
        Long userId = 999L;
        try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
            securityUtil.when(SecurityUtil::getCurrentUserId).thenReturn(userId);
            given(getUserPort.findUserByUserId(userId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> getUserService.getUser())
                    .isInstanceOf(UserNotFoundException.class);
            verify(getUserPort).findUserByUserId(userId);
        }
    }

    @Test
    @DisplayName("역할로 모든 사용자를 조회한다")
    void findAllByRole_withMiddleManagerRole_returnsAllMiddleManagers() {
        // given
        given(getUserPort.findAllUserByRole(Role.ROLE_MIDDLE_MANAGER))
                .willReturn(middleManagers);

        // when
        List<User> result = getUserService.findAllByRole(Role.ROLE_MIDDLE_MANAGER);

        // then
        assertThat(result)
                .hasSize(2)
                .containsExactlyElementsOf(middleManagers)
                .allMatch(user -> user.getRole().equals(Role.ROLE_MIDDLE_MANAGER));
        verify(getUserPort).findAllUserByRole(Role.ROLE_MIDDLE_MANAGER);
    }
}