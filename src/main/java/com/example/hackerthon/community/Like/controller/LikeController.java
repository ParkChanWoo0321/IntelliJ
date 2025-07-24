package com.example.hackerthon.community.Like.controller;

import com.example.hackerthon.comment.repository.CommentRepository;
import com.example.hackerthon.community.Like.dto.LikeResponse;
import com.example.hackerthon.community.Like.service.LikeService;
import com.example.hackerthon.community.Like.repository.LikeRepository;
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
@RequestMapping("/likes")
@RequiredArgsConstructor
public class LikeController {
    private final LikeService likeService;
    private final LikeRepository likeRepository;
    private final ScrapRepository scrapRepository;
    private final CommentRepository commentRepository;

    // 좋아요 토글
    @PostMapping("/{postId}/toggle")
    public ResponseEntity<LikeResponse> toggleLike(
            @PathVariable Long postId,
            Authentication authentication
    ) {
        String username = authentication.getName();
        LikeResponse resp = likeService.toggleLike(postId, username);
        return ResponseEntity.ok(resp);
    }

    // 내가 좋아요 누른 글 목록 조회
    @GetMapping("/mine")
    public ResponseEntity<List<PostResponse>> getLikedPosts(Authentication authentication) {
        String username = authentication.getName();
        List<Post> posts = likeService.getLikedPosts(username);

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
