package com.myme.mywarehome.domains.notification.application.service;

import com.myme.mywarehome.domains.notification.application.domain.Notification;
import com.myme.mywarehome.domains.notification.application.domain.UserNotification;
import com.myme.mywarehome.domains.notification.application.port.in.GetNotificationUseCase;
import com.myme.mywarehome.domains.notification.application.port.in.command.NotificationCommand;
import com.myme.mywarehome.domains.notification.application.port.in.result.NotificationResult;
import com.myme.mywarehome.domains.notification.application.port.out.CreateNotificationPort;
import com.myme.mywarehome.domains.notification.application.port.out.GetNotificationPort;
import com.myme.mywarehome.domains.user.application.domain.Role;
import com.myme.mywarehome.domains.user.application.domain.User;
import com.myme.mywarehome.domains.user.application.port.in.GetUserUseCase;
import com.myme.mywarehome.infrastructure.util.security.SecurityUtil;
import jakarta.transaction.Transactional;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetNotificationService implements GetNotificationUseCase {
    private final GetNotificationPort getNotificationPort;
    private final GetUserUseCase getUserUseCase;
    private final CreateNotificationPort createNotificationPort;

    @Override
    public Flux<ServerSentEvent<Object>> subscribeNotification() {
        return getNotificationPort.subscribeNotification(SecurityUtil.getCurrentUserId());
    }

    @Override
    @Transactional
    public void generateNotification(NotificationResult notificationResult, Set<Role> targetRoles) {
        // 1. Notification 엔티티 생성
        Notification notification = Notification.builder()
                .notificationType(notificationResult.type())
                .code(notificationResult.code())
                .title(notificationResult.title())
                .message(notificationResult.message())
                .build();

        // 2. Role 계층 구조에 따른 확장된 대상 Role 집합 생성
        Set<Role> expandedTargetRoles = targetRoles.stream()
                .flatMap(role -> getExpandedRoles(role).stream())
                .collect(Collectors.toSet());

        // 3. 확장된 Role을 가진 모든 사용자 조회
        Set<User> targetUsers = expandedTargetRoles.stream()
                .flatMap(role -> getUserUseCase.findAllByRole(role).stream())
                .collect(Collectors.toSet());

        // 4. 각 사용자별 UserNotification 생성
        List<UserNotification> userNotifications = targetUsers.stream()
                .map(user -> UserNotification.builder()
                        .user(user)
                        .notification(notification)
                        .isRead(false)
                        .build())
                .collect(Collectors.toList());

        // 5. 알림 저장 및 저장된 UserNotification 목록 받기
        List<UserNotification> savedNotifications = createNotificationPort.createAll(userNotifications);

        // 6. 각 사용자별로 실시간 알림 발생
        Optional<UserNotification> savedNotification = savedNotifications.stream().findFirst();
        if (savedNotification.isPresent()) {
            NotificationResult updatedResult = NotificationResult.builder()
                    .userNotificationId(savedNotification.get().getUserNotificationId())
                    .type(notification.getNotificationType())
                    .code(notification.getCode())
                    .title(notification.getTitle())
                    .message(notification.getMessage())
                    .isRead(false)
                    .createdAt(savedNotification.get().getCreatedAt())
                    .updatedAt(savedNotification.get().getUpdatedAt())
                    .build();

            // 해당 사용자의 Role에 맞는 알림만 전송
            getNotificationPort.generateNotification(updatedResult, targetRoles);
        }
    }

    Set<Role> getExpandedRoles(Role role) {
        Set<Role> expandedRoles = new HashSet<>();
        switch (role) {
            case ROLE_ADMIN -> expandedRoles.add(Role.ROLE_ADMIN);
            case ROLE_MIDDLE_MANAGER -> {
                expandedRoles.add(Role.ROLE_ADMIN);
                expandedRoles.add(Role.ROLE_MIDDLE_MANAGER);
            }
            case ROLE_WMS_MANAGER -> {
                expandedRoles.add(Role.ROLE_ADMIN);
                expandedRoles.add(Role.ROLE_MIDDLE_MANAGER);
                expandedRoles.add(Role.ROLE_WMS_MANAGER);
            }
            case ROLE_WORKER -> expandedRoles.addAll(Arrays.asList(Role.values()));
        }
        return expandedRoles;
    }

    @Override
    public void notify(NotificationCommand command) {
        NotificationResult result = NotificationResult.builder()
                .type(command.type())
                .code(command.code())
                .title(command.title())
                .message(command.message())
                .isRead(command.isRead())
                .build();

        generateNotification(result, Set.of(command.role()));
    }

    // 알림 생성을 위한 편의 메서드들
    @Override
    public void notifyToWorkers(String title, String message) {
        NotificationResult result = createNotificationResult(
                "WORKER_NOTIFICATION",
                "WORKER_TASK",
                title,
                message
        );
        generateNotification(result, Set.of(Role.ROLE_WORKER));
    }

    @Override
    public void notifyToWmsManagers(String title, String message) {
        NotificationResult result = createNotificationResult(
                "WMS_MANAGER_NOTIFICATION",
                "WMS_MANAGER_TASK",
                title,
                message
        );
        generateNotification(result, Set.of(Role.ROLE_WMS_MANAGER));
    }

    @Override
    public void notifyToMiddleManagers(String title, String message) {
        NotificationResult result = createNotificationResult(
                "MIDDLE_MANAGER_NOTIFICATION",
                "MIDDLE_MANAGER_TASK",
                title,
                message
        );
        generateNotification(result, Set.of(Role.ROLE_MIDDLE_MANAGER));
    }

    @Override
    public void notifyToAdmin(String title, String message) {
        NotificationResult result = createNotificationResult(
                "ADMIN_NOTIFICATION",
                "ADMIN_TASK",
                title,
                message
        );
        generateNotification(result, Set.of(Role.ROLE_ADMIN));
    }

    private NotificationResult createNotificationResult(
            String type,
            String code,
            String title,
            String message
    ) {
        return NotificationResult.builder()
                .type(type)
                .code(code)
                .title(title)
                .message(message)
                .isRead(false)
                .build();
    }
}
