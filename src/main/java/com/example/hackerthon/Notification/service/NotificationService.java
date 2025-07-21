package com.example.hackerthon.Notification.service;

import com.example.hackerthon.Notification.dto.NotificationDto;
import com.example.hackerthon.Notification.entity.Notification;
import com.example.hackerthon.Notification.repository.NotificationRepository;
import com.example.hackerthon.Login.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;
    // 알림 생성
    public void notify(User receiver, User sender, String message, Long postId, Long commentId) {
        Notification notification = Notification.builder()
                .receiver(receiver)
                .sender(sender)
                .message(message)
                .relatedPostId(postId)
                .relatedCommentId(commentId)
                .build();
        notificationRepository.save(notification);

        // 실시간 WebSocket 알림 전송
        messagingTemplate.convertAndSend("/topic/notify/" + receiver.getUsername(),
                new NotificationDto(notification));
    }


    // 알림 목록 조회 (최신순)
    public List<Notification> getNotifications(User receiver) {
        return notificationRepository.findByReceiverOrderByCreatedAtDesc(receiver);
    }

    // 알림 읽음 처리
    public void markAsRead(Long notificationId, User receiver) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("알림 없음"));
        if (!notification.getReceiver().equals(receiver)) {
            throw new SecurityException("본인만 읽음 처리 가능");
        }
        notification.setChecked(true);
        notificationRepository.save(notification);
    }
}
