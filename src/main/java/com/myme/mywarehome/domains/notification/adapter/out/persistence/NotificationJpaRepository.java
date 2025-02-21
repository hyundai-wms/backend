package com.myme.mywarehome.domains.notification.adapter.out.persistence;

import com.myme.mywarehome.domains.notification.application.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationJpaRepository extends JpaRepository<Notification, Long> {

}
