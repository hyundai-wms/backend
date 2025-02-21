package com.myme.mywarehome.domains.notification.adapter.out;

import com.myme.mywarehome.domains.notification.adapter.out.persistence.UserNotificationJpaRepository;
import com.myme.mywarehome.domains.notification.application.NotificationNotFoundException;
import com.myme.mywarehome.domains.notification.application.domain.UserNotification;
import com.myme.mywarehome.domains.notification.application.port.in.result.NotificationResult;
import com.myme.mywarehome.domains.notification.application.port.out.UpdateNotificationPort;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UpdateNotificationAdapter implements UpdateNotificationPort {
    private final UserNotificationJpaRepository userNotificationJpaRepository;

    @Override
    @Transactional
    public NotificationResult updateReadState(Long notificationId) {
        UserNotification notification = userNotificationJpaRepository.findByUserNotificationId(notificationId)
                .orElseThrow(NotificationNotFoundException::new);

        notification.setUserRead();

        UserNotification newNotification = userNotificationJpaRepository.save(notification);

        return NotificationResult.of(newNotification);
    }
}
