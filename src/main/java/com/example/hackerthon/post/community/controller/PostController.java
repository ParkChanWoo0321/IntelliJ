package com.example.hackerthon.post.community.controller;

import com.example.hackerthon.post.community.dto.PostRequest;
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

    // ✅ 글 작성 (사진/동영상 첨부 지원)
    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<String> createPost(
            @RequestPart("postRequest") PostRequest postRequest,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            Authentication authentication
    ) throws IOException {
        String username = authentication.getName();
        postService.createPost(postRequest, files, username);
        return ResponseEntity.ok("작성 완료");
    }

    // ✅ 글 수정 (사진/동영상 첨부 지원)
    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<String> updatePost(
            @PathVariable Long id,
            @RequestPart("postRequest") PostRequest postRequest,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            Authentication authentication
    ) throws IOException {
        String username = authentication.getName();
        postService.updatePost(id, postRequest, files, username);
        return ResponseEntity.ok("수정 완료");
    }

    // ✅ 글 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePost(
            @PathVariable Long id,
            Authentication authentication
    ) {
        String username = authentication.getName();
        postService.deletePost(id, username);
        return ResponseEntity.ok("삭제 완료");
    }

    // ✅ 단일 글 조회
    @GetMapping("/{id}")
    public ResponseEntity<?> getPost(@PathVariable Long id) {
        return ResponseEntity.ok(postService.getPost(id));
    }

    // ✅ 전체 글 목록 조회
    @GetMapping
    public ResponseEntity<?> getPosts() {
        return ResponseEntity.ok(postService.getAllPostResponses());
    }

    // ✅ 내 글만 조회
    @GetMapping("/me")
    public ResponseEntity<?> getMyPosts(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(postService.getMyPosts(username));
    }

    // ✅ 검색
    @GetMapping("/search")
    public ResponseEntity<?> searchPosts(@RequestParam String keyword) {
        return ResponseEntity.ok(postService.searchPostResponses(keyword));
    }
}
