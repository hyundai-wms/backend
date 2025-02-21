package com.myme.mywarehome.domains.notification.application.domain;

import com.myme.mywarehome.infrastructure.common.jpa.BaseTimeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "notifications")
public class Notification extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;

    private String notificationType;

    private String code;

    private String title;

    private String message;

    @Builder
    public Notification(Long notificationId, String notificationType, String code, String title, String message) {
        this.notificationId = notificationId;
        this.notificationType = notificationType;
        this.code = code;
        this.title = title;
        this.message = message;
    }
}
