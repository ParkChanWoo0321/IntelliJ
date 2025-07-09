package com.example.running.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PostResponseDto {
    private Long postId;
    private String title;
    private String content;
    private String createdAt;             // 날짜 포맷
    private List<String> imageUrls;       // 이미지 URL 리스트
    private List<String> tags;            // 태그 리스트
    private Long userId;                  // 작성자 ID
    private String nickname;              // 작성자 닉네임
}
