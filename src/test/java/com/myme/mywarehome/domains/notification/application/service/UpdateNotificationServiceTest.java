package com.myme.mywarehome.domains.notification.application.service;

import com.myme.mywarehome.domains.notification.application.port.in.result.NotificationResult;
import com.myme.mywarehome.domains.notification.application.port.out.UpdateNotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UpdateNotificationServiceTest {

    @Mock
    private UpdateNotificationPort updateNotificationPort;

    private UpdateNotificationService updateNotificationService;
    private NotificationResult testNotificationResult;

    @BeforeEach
    void setUp() {
        updateNotificationService = new UpdateNotificationService(updateNotificationPort);

        testNotificationResult = NotificationResult.builder()
                .userNotificationId(1L)
                .type("TEST_NOTIFICATION")
                .code("TEST_CODE")
                .title("테스트 알림")
                .message("테스트 메시지")
                .isRead(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("알림의 읽음 상태를 업데이트한다")
    void updateReadState_withValidNotificationId_updatesReadState() {
        // given
        Long userNotificationId = 1L;
        given(updateNotificationPort.updateReadState(userNotificationId))
                .willReturn(testNotificationResult);

        // when
        NotificationResult result = updateNotificationService.updateReadState(userNotificationId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.userNotificationId()).isEqualTo(userNotificationId);
        assertThat(result.isRead()).isTrue();
        verify(updateNotificationPort).updateReadState(userNotificationId);
    }
}