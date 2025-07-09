package com.example.running.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter

// 좋아요를 눌렀는지 여부
// 좋아요 개수
public class LikeResponseDto {
    private boolean liked;
    private int likeCount;

    public LikeResponseDto(boolean liked, int likeCount) {
        this.liked = liked;
        this.likeCount = likeCount;
    }
}
