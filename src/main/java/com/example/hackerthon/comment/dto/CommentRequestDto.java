package com.example.hackerthon.comment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
public class CommentRequestDto {
    @Setter
    @NotBlank(message = "댓글 내용을 입력해주세요.")
    private String content;
    private Long parentId; // null이면 댓글, 있으면 대댓글
}
