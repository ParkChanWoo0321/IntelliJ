package com.example.hackerthon.post.community.controller;

import com.example.hackerthon.post.community.dto.PostRequest;
import com.example.hackerthon.post.community.dto.PostResponse;
import com.example.hackerthon.post.community.entity.Post;
import com.example.hackerthon.post.community.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    // ✅ 글 작성
    @PostMapping(consumes = {"application/json"})
    public ResponseEntity<PostResponse> createPostJson(
            @RequestBody PostRequest postRequest,
            Authentication authentication
    ) throws IOException {
        String username = authentication.getName();
        Post post = postService.createPost(postRequest, null, username); // files는 null
        return ResponseEntity.ok(postService.getPost(post.getId())); // 좋아요/스크랩 포함된 PostResponse
    }

    // ✅ 글 수정
    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<PostResponse> updatePost(
            @PathVariable Long id,
            @RequestPart("postRequest") PostRequest postRequest,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            Authentication authentication
    ) throws IOException {
        String username = authentication.getName();
        Post post = postService.updatePost(id, postRequest, files, username);
        return ResponseEntity.ok(postService.getPost(post.getId()));
    }

    // ✅ 글 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePost(
            @PathVariable Long id,
            Authentication authentication
    ) {
        String username = authentication.getName();
        postService.deletePost(id, username);
        return ResponseEntity.ok().body("삭제 완료");
    }

    // ✅ 단일 글 조회
    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getPost(@PathVariable Long id) {
        return ResponseEntity.ok(postService.getPost(id));
    }

    // ✅ 전체 글 목록 조회
    @GetMapping
    public ResponseEntity<List<PostResponse>> getPosts() {
        return ResponseEntity.ok(postService.getAllPostResponses());
    }

    @GetMapping("/me")
    public ResponseEntity<List<PostResponse>> getMyPosts(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(postService.getMyPosts(username));
    }

    // ✅ 검색
    @GetMapping("/search")
    public ResponseEntity<List<PostResponse>> searchPosts(@RequestParam String keyword) {
        return ResponseEntity.ok(postService.searchPostResponses(keyword));
    }
}
