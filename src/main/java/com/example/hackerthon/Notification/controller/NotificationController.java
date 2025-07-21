package com.example.hackerthon.Notification.controller;

import com.example.hackerthon.Login.entity.User;
import com.example.hackerthon.Login.repository.UserRepository;
import com.example.hackerthon.Notification.entity.Notification;
import com.example.hackerthon.Notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final UserRepository userRepository;

    // 내 알림 목록
    @GetMapping("/my")
    public List<Notification> getMyNotifications(Authentication authentication) {
        String username = authentication.getName();
        User me = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));
        return notificationService.getNotifications(me);
    }

    // 알림 읽음 처리
    @PostMapping("/{notificationId}/read")
    public void markAsRead(
            @PathVariable Long notificationId,
            Authentication authentication
    ) {
        String username = authentication.getName();
        User me = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));
        notificationService.markAsRead(notificationId, me);
    }
}
