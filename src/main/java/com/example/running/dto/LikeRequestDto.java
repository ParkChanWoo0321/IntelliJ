package com.example.running.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter

// 좋아요를 누른 게시글 아이디를 보냄
public class LikeRequestDto {
    private Long postId;
    public LikeRequestDto(Long postId) {
        this.postId = postId;
    }
}