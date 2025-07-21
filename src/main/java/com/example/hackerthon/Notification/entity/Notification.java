package com.example.hackerthon.Notification.entity;

import com.example.hackerthon.Login.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 알림을 받는 유저
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    @JsonIgnore  // 무한 참조 방지!
    private User receiver;

    // 알림을 발생시킨 유저(예: 좋아요 누른 사람, 댓글 단 사람)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    @JsonIgnore  // 무한 참조 방지!
    private User sender;

    // 알림 메시지
    @Column(nullable = false)
    private String message;

    // (선택) 관련 게시글/댓글 등 객체의 id
    private Long relatedPostId;
    private Long relatedCommentId;

    // 읽음 여부
    private boolean checked = false;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
