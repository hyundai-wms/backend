package com.myme.mywarehome.domains.notification.application.port.out;

import com.myme.mywarehome.domains.notification.application.port.in.result.NotificationResult;
import com.myme.mywarehome.domains.user.application.domain.Role;
import java.util.Set;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;

public interface GetNotificationPort {

    Flux<ServerSentEvent<Object>> subscribeNotification(Long userId);
    void generateNotification(NotificationResult notificationResult, Set<Role> targetRoles);
}
