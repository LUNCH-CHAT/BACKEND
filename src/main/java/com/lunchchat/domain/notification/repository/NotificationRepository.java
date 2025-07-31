package com.lunchchat.domain.notification.repository;

import com.lunchchat.domain.notification.entity.Notification;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query("SELECT n FROM Notification n JOIN n.member m WHERE m.email = :email AND n.deletedAt IS NULL ORDER BY n.createdAt DESC")
    List<Notification> findFirstNotificationsByEmail(@Param("email") String email,
        Pageable pageable);

    @Query("SELECT n FROM Notification n JOIN n.member m WHERE m.email = :email AND n.deletedAt IS NULL AND n.id < :lastId ORDER BY n.createdAt DESC")
    List<Notification> findNextNotificationsByEmailAndLastId(@Param("email") String email,
        @Param("lastId") Long lastId, Pageable pageable);
}
