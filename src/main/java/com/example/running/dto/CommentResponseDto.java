package com.example.running.dto;

import lombok.Getter;
import lombok.Setter;
import com.example.running.entity.Comment;

@Getter
@Setter

// 댓글의 ID, 관련된 게시글 ID, 댓글 내용
public class CommentResponseDto {
    private Long commentId;
    private Long postId;
    private String content;

    public CommentResponseDto(Comment comment) {
        this.commentId = comment.getCommentId();
        this.postId = comment.getPost().getPostId();
        this.content = comment.getContent();
    }
}