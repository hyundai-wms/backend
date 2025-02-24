package com.myme.mywarehome.domains.notification.application.service;

import com.myme.mywarehome.domains.notification.application.domain.Notification;
import com.myme.mywarehome.domains.notification.application.domain.UserNotification;
import com.myme.mywarehome.domains.notification.application.port.in.command.NotificationCommand;
import com.myme.mywarehome.domains.notification.application.port.in.result.NotificationResult;
import com.myme.mywarehome.domains.notification.application.port.out.CreateNotificationPort;
import com.myme.mywarehome.domains.notification.application.port.out.GetNotificationPort;
import com.myme.mywarehome.domains.user.application.domain.Role;
import com.myme.mywarehome.domains.user.application.domain.User;
import com.myme.mywarehome.domains.user.application.port.in.GetUserUseCase;
import com.myme.mywarehome.infrastructure.util.security.SecurityUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetNotificationServiceTest {

    @Mock
    private GetNotificationPort getNotificationPort;
    @Mock
    private GetUserUseCase getUserUseCase;
    @Mock
    private CreateNotificationPort createNotificationPort;

    private GetNotificationService getNotificationService;
    private User adminUser;
    private User middleManagerUser;
    private User wmsManagerUser;
    private User workerUser;

    @BeforeEach
    void setUp() {
        getNotificationService = new GetNotificationService(
                getNotificationPort,
                getUserUseCase,
                createNotificationPort
        );

        adminUser = User.builder()
                .id("admin")
                .role(Role.ROLE_ADMIN)
                .build();

        middleManagerUser = User.builder()
                .id("middleManager")
                .role(Role.ROLE_MIDDLE_MANAGER)
                .build();

        wmsManagerUser = User.builder()
                .id("wmsManager")
                .role(Role.ROLE_WMS_MANAGER)
                .build();

        workerUser = User.builder()
                .id("worker")
                .role(Role.ROLE_WORKER)
                .build();
    }

    @Test
    @DisplayName("사용자 ID로 알림을 구독한다")
    void subscribeNotification_withUserId_subscribesSuccessfully() {
        // given
        Long userId = 1L;
        Flux<ServerSentEvent<Object>> expectedFlux = Flux.empty();

        try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
            securityUtil.when(SecurityUtil::getCurrentUserId).thenReturn(userId);
            given(getNotificationPort.subscribeNotification(userId)).willReturn(expectedFlux);

            // when
            Flux<ServerSentEvent<Object>> result = getNotificationService.subscribeNotification();

            // then
            StepVerifier.create(result)
                    .verifyComplete();
            verify(getNotificationPort).subscribeNotification(userId);
        }
    }

    @Test
    @DisplayName("ROLE_WORKER에게 알림을 생성하면 모든 역할의 사용자에게 전달된다")
    void generateNotification_toWorker_notifiesAllRoles() {
        // given
        NotificationResult notificationResult = createTestNotificationResult();
        List<User> allUsers = List.of(adminUser, middleManagerUser, wmsManagerUser, workerUser);
        UserNotification savedNotification = createTestUserNotification(notificationResult);

        given(getUserUseCase.findAllByRole(any())).willReturn(allUsers);
        given(createNotificationPort.createAll(any())).willReturn(List.of(savedNotification));

        // when
        getNotificationService.generateNotification(notificationResult, Set.of(Role.ROLE_WORKER));

        // then
        verify(createNotificationPort).createAll(any());
        verify(getNotificationPort).generateNotification(any(), eq(Set.of(Role.ROLE_WORKER)));
    }

    @Test
    @DisplayName("ROLE_WMS_MANAGER에게 알림을 생성하면 WMS 매니저 이상 역할의 사용자에게 전달된다")
    void generateNotification_toWmsManager_notifiesHigherRoles() {
        // given
        NotificationResult notificationResult = createTestNotificationResult();
        List<User> targetUsers = List.of(adminUser, middleManagerUser, wmsManagerUser);
        UserNotification savedNotification = createTestUserNotification(notificationResult);

        given(getUserUseCase.findAllByRole(any())).willReturn(targetUsers);
        given(createNotificationPort.createAll(any())).willReturn(List.of(savedNotification));

        // when
        getNotificationService.generateNotification(notificationResult, Set.of(Role.ROLE_WMS_MANAGER));

        // then
        verify(createNotificationPort).createAll(any());
        verify(getNotificationPort).generateNotification(any(), eq(Set.of(Role.ROLE_WMS_MANAGER)));
    }

    @Test
    @DisplayName("알림 명령을 받아 해당 역할에 맞는 알림을 생성한다")
    void notify_withCommand_generatesNotification() {
        // given
        NotificationCommand command = new NotificationCommand(
                "TEST_TYPE",
                "TEST_CODE",
                "테스트 제목",
                "테스트 메시지",
                false,
                Role.ROLE_ADMIN
        );

        UserNotification savedNotification = UserNotification.builder()
                .userNotificationId(1L)
                .notification(Notification.builder()
                        .notificationType(command.type())
                        .code(command.code())
                        .title(command.title())
                        .message(command.message())
                        .build())
                .user(adminUser)
                .isRead(false)
                .build();

        given(getUserUseCase.findAllByRole(Role.ROLE_ADMIN)).willReturn(List.of(adminUser));
        given(createNotificationPort.createAll(any())).willReturn(List.of(savedNotification));

        // when
        getNotificationService.notify(command);

        // then
        verify(createNotificationPort).createAll(any());
        verify(getNotificationPort).generateNotification(any(), eq(Set.of(Role.ROLE_ADMIN)));
    }

    @Test
    @DisplayName("Role에 따른 확장된 역할 집합을 올바르게 반환한다")
    void getExpandedRoles_returnsCorrectExpandedRoles() {
        // given & when & then
        Set<Role> workerRoles = getNotificationService.getExpandedRoles(Role.ROLE_WORKER);
        assertThat(workerRoles).containsExactlyInAnyOrder(Role.values());

        Set<Role> wmsManagerRoles = getNotificationService.getExpandedRoles(Role.ROLE_WMS_MANAGER);
        assertThat(wmsManagerRoles).containsExactlyInAnyOrder(
                Role.ROLE_ADMIN,
                Role.ROLE_MIDDLE_MANAGER,
                Role.ROLE_WMS_MANAGER
        );

        Set<Role> middleManagerRoles = getNotificationService.getExpandedRoles(Role.ROLE_MIDDLE_MANAGER);
        assertThat(middleManagerRoles).containsExactlyInAnyOrder(
                Role.ROLE_ADMIN,
                Role.ROLE_MIDDLE_MANAGER
        );

        Set<Role> adminRoles = getNotificationService.getExpandedRoles(Role.ROLE_ADMIN);
        assertThat(adminRoles).containsExactly(Role.ROLE_ADMIN);
    }

    @Test
    @DisplayName("작업자에게 알림을 전송한다")
    void notifyToWorkers_sendsNotificationToWorkers() {
        // given
        String title = "작업 알림";
        String message = "새로운 작업이 할당되었습니다";
        UserNotification savedNotification = createTestUserNotification(
                createTestNotificationResult("WORKER_NOTIFICATION", "WORKER_TASK", title, message)
        );

        given(getUserUseCase.findAllByRole(any())).willReturn(List.of(workerUser));
        given(createNotificationPort.createAll(any())).willReturn(List.of(savedNotification));

        // when
        getNotificationService.notifyToWorkers(title, message);

        // then
        verify(createNotificationPort).createAll(any());
        verify(getNotificationPort).generateNotification(any(), eq(Set.of(Role.ROLE_WORKER)));
    }

    @Test
    @DisplayName("WMS 관리자에게 알림을 전송한다")
    void notifyToWmsManagers_sendsNotificationToWmsManagers() {
        // given
        String title = "WMS 알림";
        String message = "재고 부족 알림";
        UserNotification savedNotification = createTestUserNotification(
                createTestNotificationResult("WMS_MANAGER_NOTIFICATION", "WMS_MANAGER_TASK", title, message)
        );

        given(getUserUseCase.findAllByRole(any())).willReturn(List.of(wmsManagerUser));
        given(createNotificationPort.createAll(any())).willReturn(List.of(savedNotification));

        // when
        getNotificationService.notifyToWmsManagers(title, message);

        // then
        verify(createNotificationPort).createAll(any());
        verify(getNotificationPort).generateNotification(any(), eq(Set.of(Role.ROLE_WMS_MANAGER)));
    }

    @Test
    @DisplayName("중간 관리자에게 알림을 전송한다")
    void notifyToMiddleManagers_sendsNotificationToMiddleManagers() {
        // given
        String title = "관리자 알림";
        String message = "승인 요청";
        UserNotification savedNotification = createTestUserNotification(
                createTestNotificationResult("MIDDLE_MANAGER_NOTIFICATION", "MIDDLE_MANAGER_TASK", title, message)
        );

        given(getUserUseCase.findAllByRole(any())).willReturn(List.of(middleManagerUser));
        given(createNotificationPort.createAll(any())).willReturn(List.of(savedNotification));

        // when
        getNotificationService.notifyToMiddleManagers(title, message);

        // then
        verify(createNotificationPort).createAll(any());
        verify(getNotificationPort).generateNotification(any(), eq(Set.of(Role.ROLE_MIDDLE_MANAGER)));
    }

    @Test
    @DisplayName("최고 관리자에게 알림을 전송한다")
    void notifyToAdmin_sendsNotificationToAdmin() {
        // given
        String title = "시스템 알림";
        String message = "시스템 점검 예정";
        UserNotification savedNotification = createTestUserNotification(
                createTestNotificationResult("ADMIN_NOTIFICATION", "ADMIN_TASK", title, message)
        );

        given(getUserUseCase.findAllByRole(any())).willReturn(List.of(adminUser));
        given(createNotificationPort.createAll(any())).willReturn(List.of(savedNotification));

        // when
        getNotificationService.notifyToAdmin(title, message);

        // then
        verify(createNotificationPort).createAll(any());
        verify(getNotificationPort).generateNotification(any(), eq(Set.of(Role.ROLE_ADMIN)));
    }

    // Helper method for creating test notification results with specific parameters
    private NotificationResult createTestNotificationResult(String type, String code, String title, String message) {
        return NotificationResult.builder()
                .type(type)
                .code(code)
                .title(title)
                .message(message)
                .isRead(false)
                .build();
    }

    private NotificationResult createTestNotificationResult() {
        return NotificationResult.builder()
                .type("TEST_NOTIFICATION")
                .code("TEST_CODE")
                .title("테스트 알림")
                .message("테스트 메시지")
                .isRead(false)
                .build();
    }

    private UserNotification createTestUserNotification(NotificationResult result) {
        return UserNotification.builder()
                .userNotificationId(1L)
                .notification(Notification.builder()
                        .notificationType(result.type())
                        .code(result.code())
                        .title(result.title())
                        .message(result.message())
                        .build())
                .user(workerUser)
                .isRead(false)
                .build();
    }
}