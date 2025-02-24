package com.myme.mywarehome.domains.user.application.service;

import com.myme.mywarehome.domains.user.application.domain.Role;
import com.myme.mywarehome.domains.user.application.domain.User;
import com.myme.mywarehome.domains.user.application.dto.in.UpdateUserRoleCommand;
import com.myme.mywarehome.domains.user.application.exception.UserNotFoundException;
import com.myme.mywarehome.domains.user.application.exception.UserRoleUpdateFailedException;
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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateUserRoleServiceTest {

    @Mock
    private GetUserPort getUserPort;
    @Mock
    private UpdateUserPort updateUserPort;
    @Mock
    private SessionService sessionService;

    private UpdateUserRoleService updateUserRoleService;
    private User testUser;
    private User adminUser;

    @BeforeEach
    void setUp() {
        updateUserRoleService = new UpdateUserRoleService(
                getUserPort,
                updateUserPort,
                sessionService
        );

        testUser = User.builder()
                .id("testId")
                .name("테스트")
                .phoneNumber("01012345678")
                .role(Role.ROLE_WMS_MANAGER)
                .build();

        adminUser = User.builder()
                .id("adminId")
                .name("관리자")
                .phoneNumber("01087654321")
                .role(Role.ROLE_ADMIN)
                .build();
    }

    @Test
    @DisplayName("일반 사용자의 역할을 변경한다")
    void updateRole_withNormalUser_updatesRole() {
        // given
        Long userId = 1L;
        Role newRole = Role.ROLE_MIDDLE_MANAGER;
        UpdateUserRoleCommand command = new UpdateUserRoleCommand(userId, newRole);

        given(getUserPort.findUserByUserId(userId)).willReturn(Optional.of(testUser));
        given(updateUserPort.updateUser(any(User.class))).willReturn(Optional.of(testUser));

        // when
        User result = updateUserRoleService.updateRole(command);

        // then
        assertThat(result.getRole()).isEqualTo(newRole);
        verify(sessionService).updateUserSessionRole(userId, newRole);
        verify(updateUserPort, never()).updateUserRoleToMiddleManager(any());
    }

    @Test
    @DisplayName("관리자 권한으로 역할 변경시 현재 관리자의 권한이 변경된다")
    void updateRole_toAdminRole_updatesBothUsersRoles() {
        // given
        Long targetUserId = 1L;
        Long currentAdminId = 2L;
        UpdateUserRoleCommand command = new UpdateUserRoleCommand(targetUserId, Role.ROLE_ADMIN);

        try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
            securityUtil.when(SecurityUtil::getCurrentUserId).thenReturn(currentAdminId);

            given(getUserPort.findUserByUserId(targetUserId)).willReturn(Optional.of(testUser));
            given(updateUserPort.updateUser(any(User.class))).willReturn(Optional.of(testUser));

            // when
            User result = updateUserRoleService.updateRole(command);

            // then
            assertThat(result.getRole()).isEqualTo(Role.ROLE_ADMIN);
            verify(updateUserPort).updateUserRoleToMiddleManager(currentAdminId);
            verify(sessionService).updateUserSessionRole(currentAdminId, Role.ROLE_MIDDLE_MANAGER);
            verify(sessionService).updateUserSessionRole(targetUserId, Role.ROLE_ADMIN);
        }
    }

    @Test
    @DisplayName("관리자의 역할 변경 시도시 예외가 발생한다")
    void updateRole_withAdminUser_throwsUserRoleUpdateFailedException() {
        // given
        Long adminUserId = 1L;
        UpdateUserRoleCommand command = new UpdateUserRoleCommand(adminUserId, Role.ROLE_MIDDLE_MANAGER);

        given(getUserPort.findUserByUserId(adminUserId)).willReturn(Optional.of(adminUser));

        // when & then
        assertThatThrownBy(() -> updateUserRoleService.updateRole(command))
                .isInstanceOf(UserRoleUpdateFailedException.class);

        verify(updateUserPort, never()).updateUser(any());
        verify(sessionService, never()).updateUserSessionRole(any(), any());
    }

    @Test
    @DisplayName("존재하지 않는 사용자의 역할 변경 시도시 예외가 발생한다")
    void updateRole_withNonExistentUser_throwsUserNotFoundException() {
        // given
        Long nonExistentUserId = 999L;
        UpdateUserRoleCommand command = new UpdateUserRoleCommand(nonExistentUserId, Role.ROLE_WMS_MANAGER);

        given(getUserPort.findUserByUserId(nonExistentUserId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> updateUserRoleService.updateRole(command))
                .isInstanceOf(UserNotFoundException.class);

        verify(updateUserPort, never()).updateUser(any());
        verify(sessionService, never()).updateUserSessionRole(any(), any());
    }

    @Test
    @DisplayName("사용자 업데이트 실패시 예외가 발생한다")
    void updateRole_whenUpdateFails_throwsUserNotFoundException() {
        // given
        Long userId = 1L;
        UpdateUserRoleCommand command = new UpdateUserRoleCommand(userId, Role.ROLE_WMS_MANAGER);

        given(getUserPort.findUserByUserId(userId)).willReturn(Optional.of(testUser));
        given(updateUserPort.updateUser(any(User.class))).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> updateUserRoleService.updateRole(command))
                .isInstanceOf(UserNotFoundException.class);

        verify(sessionService, never()).updateUserSessionRole(any(), any());
    }
}