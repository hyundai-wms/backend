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
    public List<UserNotification> createAll(List<UserNotification> notifications) {
        // 1. Notification 엔티티 추출 및 저장
        Notification notification = notifications.get(0).getNotification();
        Notification savedNotification = notificationJpaRepository.save(notification);

        // 2. UserNotification 업데이트 및 저장
        List<UserNotification> updatedNotifications = notifications.stream()
                .map(un -> UserNotification.builder()
                        .user(un.getUser())
                        .notification(savedNotification)
                        .isRead(false)
                        .build())
                .collect(Collectors.toList());

        // 3. 저장하고 저장된 UserNotification 목록 반환
        return userNotificationJpaRepository.saveAll(updatedNotifications);
    }
}
