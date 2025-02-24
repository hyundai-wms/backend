package com.myme.mywarehome.domains.user.application.service;

import com.myme.mywarehome.domains.user.application.domain.Role;
import com.myme.mywarehome.domains.user.application.domain.User;
import com.myme.mywarehome.domains.user.application.exception.UserDeletionFailedException;
import com.myme.mywarehome.domains.user.application.exception.UserNotFoundException;
import com.myme.mywarehome.domains.user.application.port.out.DeleteUserPort;
import com.myme.mywarehome.domains.user.application.port.out.GetUserPort;
import com.myme.mywarehome.infrastructure.util.security.session.SessionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class DeleteUserServiceTest {

    @Mock
    private GetUserPort getUserPort;
    @Mock
    private DeleteUserPort deleteUserPort;
    @Mock
    private SessionService sessionService;

    private DeleteUserService deleteUserService;
    private User normalUser;
    private User adminUser;

    @BeforeEach
    void setUp() {
        deleteUserService = new DeleteUserService(
                getUserPort,
                deleteUserPort,
                sessionService
        );

        normalUser = User.builder()
                .id("normalId")
                .phoneNumber("01012345678")
                .name("일반유저")
                .role(Role.ROLE_WMS_MANAGER)
                .build();

        adminUser = User.builder()
                .id("adminId")
                .phoneNumber("01087654321")
                .name("관리자")
                .role(Role.ROLE_ADMIN)
                .build();
    }

    @Test
    @DisplayName("일반 사용자를 성공적으로 삭제한다")
    void deleteUser_withNormalUser_deletesSuccessfully() {
        // given
        Long userId = 1L;
        given(getUserPort.findUserByUserId(userId)).willReturn(Optional.of(normalUser));

        // when
        deleteUserService.deleteUser(userId);

        // then
        verify(deleteUserPort).delete(userId);
        verify(sessionService).deleteUserSessions(userId);
    }

    @Test
    @DisplayName("관리자 계정 삭제 시도시 예외가 발생한다")
    void deleteUser_withAdminUser_throwsUserDeletionFailedException() {
        // given
        Long userId = 1L;
        given(getUserPort.findUserByUserId(userId)).willReturn(Optional.of(adminUser));

        // when & then
        assertThatThrownBy(() -> deleteUserService.deleteUser(userId))
                .isInstanceOf(UserDeletionFailedException.class);

        verify(deleteUserPort, never()).delete(any());
        verify(sessionService, never()).deleteUserSessions(any());
    }

    @Test
    @DisplayName("존재하지 않는 사용자 삭제 시도시 예외가 발생한다")
    void deleteUser_withNonExistentUser_throwsUserNotFoundException() {
        // given
        Long userId = 999L;
        given(getUserPort.findUserByUserId(userId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> deleteUserService.deleteUser(userId))
                .isInstanceOf(UserNotFoundException.class);

        verify(deleteUserPort, never()).delete(any());
        verify(sessionService, never()).deleteUserSessions(any());
    }
}