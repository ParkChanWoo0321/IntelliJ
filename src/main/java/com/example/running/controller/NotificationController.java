package com.example.running.controller;

import com.example.running.dto.NotificationResponseDto;
import com.example.running.entity.User;
import com.example.running.repository.UserRepository;
import com.example.running.service.NotificationService;
import com.example.running.service.SseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final SseService sseService;
    private final NotificationService notificationService;
    private final UserRepository userRepository;  // UserService 대신 직접 사용

    // SSE 연결
    @GetMapping("/subscribe")
    public SseEmitter subscribe(@RequestParam Long userId) {
        return sseService.subscribe(userId);
    }

    // 알림 목록 조회
    @GetMapping
    public List<NotificationResponseDto> getAll(@RequestParam Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        return notificationService.getNotifications(user);
    }
}
