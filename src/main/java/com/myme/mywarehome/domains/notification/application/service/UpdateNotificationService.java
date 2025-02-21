package com.myme.mywarehome.domains.notification.application.service;

import com.myme.mywarehome.domains.notification.application.port.in.UpdateNotificationUseCase;
import com.myme.mywarehome.domains.notification.application.port.in.result.NotificationResult;
import com.myme.mywarehome.domains.notification.application.port.out.UpdateNotificationPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateNotificationService implements UpdateNotificationUseCase {
    private final UpdateNotificationPort updateNotificationPort;

    @Override
    public NotificationResult updateReadState(Long userNotificationId) {
        return updateNotificationPort.updateReadState(userNotificationId);
    }
}
