package com.myme.mywarehome.domains.notification.application.domain;

import com.myme.mywarehome.domains.user.application.domain.User;
import com.myme.mywarehome.infrastructure.common.jpa.BaseTimeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user_notifications")
public class UserNotification extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userNotificationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notification_id")
    private Notification notification;

    private Boolean isRead;

    @Builder
    public UserNotification(Long userNotificationId, User user, Notification notification, Boolean isRead) {
        this.userNotificationId = userNotificationId;
        this.user = user;
        this.notification = notification;
        this.isRead = isRead;
    }

    public void setUserRead() {
        isRead = true;
    }
}
