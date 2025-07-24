package com.example.hackerthon.community.Like.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class LikeResponse {
    private Long postId;
    private int likeCount;
    private boolean liked;
}
