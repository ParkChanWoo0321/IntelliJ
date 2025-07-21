package com.example.hackerthon.Notification.repository;

import com.example.hackerthon.Notification.entity.Notification;
import com.example.hackerthon.Login.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByReceiverOrderByCreatedAtDesc(User receiver);
}
