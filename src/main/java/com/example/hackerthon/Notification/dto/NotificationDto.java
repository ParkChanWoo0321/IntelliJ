package com.example.hackerthon.Notification.dto;

import com.example.hackerthon.Notification.entity.Notification;
import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {
    private Long id;
    private String message;
    private String sender;
    private boolean checked;
    private Long postId;
    private Long commentId;

    public NotificationDto(Notification n) {
        this.id = n.getId();
        this.message = n.getMessage();
        this.sender = n.getSender().getUsername();
        this.checked = n.isChecked();
        this.postId = n.getRelatedPostId();
        this.commentId = n.getRelatedCommentId();
    }
}