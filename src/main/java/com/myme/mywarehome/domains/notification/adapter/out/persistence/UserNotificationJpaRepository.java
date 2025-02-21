package com.myme.mywarehome.domains.notification.adapter.out.persistence;

import com.myme.mywarehome.domains.notification.application.domain.UserNotification;
import com.myme.mywarehome.domains.user.application.domain.Role;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserNotificationJpaRepository extends JpaRepository<UserNotification, Long> {

    @Query("""
            SELECT DISTINCT un FROM UserNotification un
            JOIN FETCH un.notification n
            JOIN FETCH un.user u
            WHERE
                (u.role = :role) OR
                (:role = com.myme.mywarehome.domains.user.application.domain.Role.ROLE_ADMIN) OR
                (:role = com.myme.mywarehome.domains.user.application.domain.Role.ROLE_MIDDLE_MANAGER AND u.role IN 
                    (com.myme.mywarehome.domains.user.application.domain.Role.ROLE_MIDDLE_MANAGER,
                    com.myme.mywarehome.domains.user.application.domain.Role.ROLE_WMS_MANAGER,
                    com.myme.mywarehome.domains.user.application.domain.Role.ROLE_WORKER)) OR
                (:role = com.myme.mywarehome.domains.user.application.domain.Role.ROLE_WMS_MANAGER AND u.role IN 
                    (com.myme.mywarehome.domains.user.application.domain.Role.ROLE_WMS_MANAGER,
                    com.myme.mywarehome.domains.user.application.domain.Role.ROLE_WORKER))
            ORDER BY un.createdAt DESC
            """)
    List<UserNotification> findTop5ByUserRoleOrderByCreatedAtDesc(Role role, Pageable pageable);

    default List<UserNotification> findTop5ByUserRoleOrderByCreatedAtDesc(Role role) {
        return findTop5ByUserRoleOrderByCreatedAtDesc(role, PageRequest.of(0, 5));
    }
}
