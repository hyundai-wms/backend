package com.myme.mywarehome.domains.notification.adapter.out;

import com.myme.mywarehome.domains.notification.adapter.out.persistence.NotificationJpaRepository;
import com.myme.mywarehome.domains.notification.adapter.out.persistence.UserNotificationJpaRepository;
import com.myme.mywarehome.domains.notification.application.domain.Notification;
import com.myme.mywarehome.domains.notification.application.domain.UserNotification;
import com.myme.mywarehome.domains.notification.application.port.out.CreateNotificationPort;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateNotificationAdapter implements CreateNotificationPort {
    private final NotificationJpaRepository notificationJpaRepository;
    private final UserNotificationJpaRepository userNotificationJpaRepository;

    @Override
    public void create(Notification notification) {
        notificationJpaRepository.save(notification);
    }

    @Override
    @Transactional
    public void createAll(List<UserNotification> notifications) {
        // 1. 먼저 모든 Notification 엔티티들을 추출하여 저장
        Set<Notification> uniqueNotifications = notifications.stream()
                .map(UserNotification::getNotification)
                .collect(Collectors.toSet());

        notificationJpaRepository.saveAll(uniqueNotifications);

        // 2. 그 다음 UserNotification 저장
        userNotificationJpaRepository.saveAll(notifications);
    }
}
