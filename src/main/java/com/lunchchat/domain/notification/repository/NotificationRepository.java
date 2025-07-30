package com.lunchchat.domain.notification.repository;

import com.lunchchat.domain.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    @Query("SELECT n FROM Notification n JOIN n.member m WHERE m.email = :email AND n.deletedAt IS NULL ORDER BY n.createdAt DESC")
    List<Notification> findByUserEmailOrderByCreatedAtDesc(@Param("email") String email);
}
