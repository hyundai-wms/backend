package com.myme.mywarehome.domains.notification.adapter.in.web;

import com.myme.mywarehome.domains.notification.adapter.in.web.request.NotificationRequest;
import com.myme.mywarehome.domains.notification.application.port.in.GetNotificationUseCase;
import com.myme.mywarehome.domains.notification.application.port.in.UpdateNotificationUseCase;
import com.myme.mywarehome.domains.notification.application.port.in.result.NotificationResult;
import com.myme.mywarehome.domains.notification.application.port.out.UpdateNotificationPort;
import com.myme.mywarehome.infrastructure.common.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final GetNotificationUseCase getNotificationUseCase;
    private final UpdateNotificationUseCase updateNotificationUseCase;

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<Object>> streamNotifications() {
        return getNotificationUseCase.subscribeNotification();
    }

    @PatchMapping("/{userNotificationId}")
    public CommonResponse<NotificationResult> updateReadState(
            @PathVariable Long userNotificationId
    ) {
        return CommonResponse.from(
                updateNotificationUseCase.updateReadState(userNotificationId)
        );
    }

    @PostMapping("/manually-send")
    public CommonResponse<Void> notifyTest(
            @RequestBody NotificationRequest notificationRequest
    ) {
        // 작업자에게 작업 할당 알림
        getNotificationUseCase.notify(notificationRequest.toCommand());

        return CommonResponse.empty();
    }

    @PostMapping("/worker-test")
    public CommonResponse<Void> workerNotifyTest() {
        // 작업자에게 작업 할당 알림
        getNotificationUseCase.notifyToWorkers(
                "WORKER 테스트 알림",
                "WMS 시스템 내의 모든 WORKER에게 알림이 발송됩니다."
        );

        return CommonResponse.empty();
    }

    @PostMapping("/wms-manager-test")
    public CommonResponse<Void> wmsManagerNotifyTest() {
        // 작업자에게 작업 할당 알림
        getNotificationUseCase.notifyToWmsManagers(
                "WMS MANAGER 테스트 알림",
                "WMS 시스템 내의 모든 WMS MANAGER에게 알림이 발송됩니다."
        );

        return CommonResponse.empty();
    }

    @PostMapping("/middle-manager-test")
    public CommonResponse<Void> middleManagerNotifyTest() {
        // 작업자에게 작업 할당 알림
        getNotificationUseCase.notifyToMiddleManagers(
                "MIDDLE MANAGER 테스트 알림",
                "WMS 시스템 내의 모든 MIDDLE MANAGER에게 알림이 발송됩니다."
        );

        return CommonResponse.empty();
    }

    @PostMapping("/admin-test")
    public CommonResponse<Void> AdminNotifyTest() {
        // 작업자에게 작업 할당 알림
        getNotificationUseCase.notifyToAdmin(
                "ADMIN 테스트 알림",
                "WMS 시스템 내의 모든 ADMIN에게 알림이 발송됩니다."
        );

        return CommonResponse.empty();
    }

}
