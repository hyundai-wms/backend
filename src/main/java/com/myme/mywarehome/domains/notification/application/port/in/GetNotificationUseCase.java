package com.myme.mywarehome.domains.notification.application.port.in;

import com.myme.mywarehome.domains.notification.application.port.in.command.NotificationCommand;
import com.myme.mywarehome.domains.notification.application.port.in.result.NotificationResult;
import com.myme.mywarehome.domains.user.application.domain.Role;
import java.util.Set;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;

public interface GetNotificationUseCase {
    Flux<ServerSentEvent<Object>> subscribeNotification();
    void generateNotification(NotificationResult notificationResult, Set<Role> targetRoles);
    void notify(NotificationCommand command);
    void notifyToWorkers(String title, String message);
    void notifyToWmsManagers(String title, String message);
    void notifyToMiddleManagers(String title, String message);
    void notifyToAdmin(String title, String message);
}
