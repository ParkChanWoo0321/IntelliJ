package org.example.simplememo.dto;

import lombok.Getter;
import lombok.Setter;
import org.example.simplememo.entity.Comment;

@Getter
@Setter
public class CommentResponseDto {
    private Long commentId;
    private Long memoId;
    private String content;

    public CommentResponseDto(Comment comment) {
        this.commentId = comment.getCommentId();
        this.memoId = comment.getMemo().getMemoId();
        this.content = comment.getContent();
    }
}
