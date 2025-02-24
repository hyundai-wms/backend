package com.myme.mywarehome.domains.user.application.service;

import com.myme.mywarehome.domains.user.adapter.out.exception.InvalidCurrentPasswordException;
import com.myme.mywarehome.domains.user.application.domain.Role;
import com.myme.mywarehome.domains.user.application.domain.User;
import com.myme.mywarehome.domains.user.application.exception.UserNotFoundException;
import com.myme.mywarehome.domains.user.application.port.out.GetUserPort;
import com.myme.mywarehome.domains.user.application.port.out.UpdateUserPort;
import com.myme.mywarehome.infrastructure.util.security.SecurityUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateUserPasswordServiceTest {

    @Mock
    private GetUserPort getUserPort;
    @Mock
    private UpdateUserPort updateUserPort;
    @Mock
    private PasswordEncoder passwordEncoder;

    private UpdateUserPasswordService updateUserPasswordService;
    private User testUser;

    @BeforeEach
    void setUp() {
        updateUserPasswordService = new UpdateUserPasswordService(
                getUserPort,
                updateUserPort,
                passwordEncoder
        );

        testUser = User.builder()
                .id("testId")
                .password("encodedOldPassword")
                .name("테스트")
                .phoneNumber("01012345678")
                .role(Role.ROLE_WMS_MANAGER)
                .build();
    }

    @Test
    @DisplayName("올바른 현재 비밀번호로 새로운 비밀번호로 변경한다")
    void updatePassword_withValidCurrentPassword_updatesPassword() {
        // given
        Long userId = 1L;
        String oldPassword = "oldPassword";
        String newPassword = "newPassword";
        String encodedNewPassword = "encodedNewPassword";

        try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
            securityUtil.when(SecurityUtil::getCurrentUserId).thenReturn(userId);

            given(getUserPort.findUserByUserId(userId)).willReturn(Optional.of(testUser));
            given(passwordEncoder.matches(oldPassword, testUser.getPassword())).willReturn(true);
            given(passwordEncoder.encode(newPassword)).willReturn(encodedNewPassword);

            // when
            updateUserPasswordService.updatePassword(oldPassword, newPassword);

            // then
            verify(updateUserPort).updateUser(argThat(user ->
                    user.getPassword().equals(encodedNewPassword)
            ));
        }
    }

    @Test
    @DisplayName("잘못된 현재 비밀번호로 변경 시도시 예외가 발생한다")
    void updatePassword_withInvalidCurrentPassword_throwsInvalidCurrentPasswordException() {
        // given
        Long userId = 1L;
        String wrongOldPassword = "wrongPassword";
        String newPassword = "newPassword";

        try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
            securityUtil.when(SecurityUtil::getCurrentUserId).thenReturn(userId);

            given(getUserPort.findUserByUserId(userId)).willReturn(Optional.of(testUser));
            given(passwordEncoder.matches(wrongOldPassword, testUser.getPassword())).willReturn(false);

            // when & then
            assertThatThrownBy(() ->
                    updateUserPasswordService.updatePassword(wrongOldPassword, newPassword)
            ).isInstanceOf(InvalidCurrentPasswordException.class);

            verify(passwordEncoder, never()).encode(anyString());
            verify(updateUserPort, never()).updateUser(any());
        }
    }

    @Test
    @DisplayName("존재하지 않는 사용자의 비밀번호 변경 시도시 예외가 발생한다")
    void updatePassword_withNonExistentUser_throwsUserNotFoundException() {
        // given
        Long userId = 999L;
        String oldPassword = "oldPassword";
        String newPassword = "newPassword";

        try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
            securityUtil.when(SecurityUtil::getCurrentUserId).thenReturn(userId);

            given(getUserPort.findUserByUserId(userId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() ->
                    updateUserPasswordService.updatePassword(oldPassword, newPassword)
            ).isInstanceOf(UserNotFoundException.class);

            verify(passwordEncoder, never()).matches(anyString(), anyString());
            verify(passwordEncoder, never()).encode(anyString());
            verify(updateUserPort, never()).updateUser(any());
        }
    }
}