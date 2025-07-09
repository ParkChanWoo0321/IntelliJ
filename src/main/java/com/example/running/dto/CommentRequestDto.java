package com.example.running.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor

// 댓글 내용을 보냄
public class CommentRequestDto {
    private String content;
}
