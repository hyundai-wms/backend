package com.myme.mywarehome.domains.notification.adapter.out;

import com.myme.mywarehome.domains.notification.adapter.out.event.UserNotificationEvent;
import com.myme.mywarehome.domains.notification.adapter.out.persistence.UserNotificationJpaRepository;
import com.myme.mywarehome.domains.notification.application.port.in.result.NotificationResult;
import com.myme.mywarehome.domains.notification.application.port.out.GetNotificationPort;
import com.myme.mywarehome.domains.user.adapter.out.persistence.UserJpaRepository;
import com.myme.mywarehome.domains.user.application.domain.Role;
import com.myme.mywarehome.domains.user.application.domain.User;
import com.myme.mywarehome.domains.user.application.exception.UserNotFoundException;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Component
@RequiredArgsConstructor
public class GetNotificationAdapter implements GetNotificationPort {
    private final UserNotificationJpaRepository userNotificationJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final Sinks.Many<UserNotificationEvent> sinks = Sinks.many().multicast().onBackpressureBuffer();

    @Override
    public Flux<ServerSentEvent<Object>> subscribeNotification(Long userId) {
        // 1. User 조회를 Mono로 변환
        Mono<User> userMono = Mono.fromSupplier(() ->
                userJpaRepository.findById(userId)
                        .orElseThrow(UserNotFoundException::new)
        ).subscribeOn(Schedulers.boundedElastic());

        // 2. 초기 알림 조회를 Flux로 변환
        Flux<ServerSentEvent<Object>> initialNotifications = userMono
                .flatMapMany(user -> Flux.fromIterable(
                        userNotificationJpaRepository.findTop5ByUserRoleOrderByCreatedAtDesc(user.getRole())
                ))
                .map(notification -> NotificationResult.builder()
                        .userNotificationId(notification.getUserNotificationId())
                        .type(notification.getNotification().getNotificationType())
                        .code(notification.getNotification().getCode())
                        .title(notification.getNotification().getTitle())
                        .message(notification.getNotification().getMessage())
                        .isRead(notification.getIsRead())
                        .build()
                )
                .collectList()
                .map(notifications -> ServerSentEvent.builder()
                        .event("initial")
                        .data(notifications)
                        .build())
                .flux()
                .subscribeOn(Schedulers.boundedElastic());

        // 3. 실시간 알림 구독
        Flux<ServerSentEvent<Object>> updates = userMono
                .flatMapMany(user -> sinks.asFlux()
                        .filter(event -> isRoleEligibleForNotification(user.getRole(), event.targetRoles()))
                        .map(event -> ServerSentEvent.builder()
                                .event("notification")
                                .data(event.data())
                                .build()))
                .onErrorResume(error -> {
                    log.error("Error in user stream:", error);
                    return Flux.empty(); // 에러 발생 시 빈 Flux 반환하여 스트림 유지
                });

        // userMono가 empty인 경우도 처리
        Flux<ServerSentEvent<Object>> safeUpdates = userMono
                .switchIfEmpty(Mono.error(new RuntimeException("User not found")))
                .flatMapMany(user -> updates);

        return Flux.concat(initialNotifications, safeUpdates);
    }

    @Override
    public void generateNotification(NotificationResult notificationResult, Set<Role> targetRoles) {
        sinks.tryEmitNext(new UserNotificationEvent(targetRoles, notificationResult));
    }

    private boolean isRoleEligibleForNotification(Role userRole, Set<Role> targetRoles) {
        return switch (userRole) {
            case ROLE_ADMIN -> true;
            case ROLE_MIDDLE_MANAGER -> targetRoles.contains(Role.ROLE_MIDDLE_MANAGER) ||
                    targetRoles.contains(Role.ROLE_WMS_MANAGER) ||
                    targetRoles.contains(Role.ROLE_WORKER);
            case ROLE_WMS_MANAGER -> targetRoles.contains(Role.ROLE_WMS_MANAGER) ||
                    targetRoles.contains(Role.ROLE_WORKER);
            case ROLE_WORKER -> targetRoles.contains(Role.ROLE_WORKER);
        };
    }
}
