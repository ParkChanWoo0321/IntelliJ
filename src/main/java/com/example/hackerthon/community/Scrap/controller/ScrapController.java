package com.example.hackerthon.community.Scrap.controller;

import com.example.hackerthon.comment.repository.CommentRepository;
import com.example.hackerthon.community.Like.repository.LikeRepository;
import com.example.hackerthon.community.Scrap.service.ScrapService;
import com.example.hackerthon.community.Scrap.repository.ScrapRepository;
import com.example.hackerthon.community.post.dto.PostResponse;
import com.example.hackerthon.community.post.entity.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/scraps")
@RequiredArgsConstructor
public class ScrapController {
    private final ScrapService scrapService;
    private final LikeRepository likeRepository;
    private final ScrapRepository scrapRepository;
    private final CommentRepository commentRepository;

    // ⭐ 스크랩 토글
    @PostMapping("/{postId}/toggle")
    public ResponseEntity<String> toggleScrap(
            @PathVariable Long postId,
            Authentication authentication
    ) {
        String username = authentication.getName();
        boolean scrapped = scrapService.toggleScrap(postId, username);
        return ResponseEntity.ok(scrapped ? "스크랩 완료" : "스크랩 취소");
    }

    // ⭐ 내가 스크랩한 글 목록 조회
    @GetMapping("/mine")
    public ResponseEntity<List<PostResponse>> getScrappedPosts(Authentication authentication) {
        String username = authentication.getName();
        List<Post> posts = scrapService.getScrappedPosts(username);

        List<PostResponse> responses = posts.stream()
                .map(post -> new PostResponse(
                        post,
                        likeRepository.countByPost(post),
                        scrapRepository.countByPost(post),
                        commentRepository.countByPost(post)
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }
}
