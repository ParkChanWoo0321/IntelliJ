package com.example.hackerthon.comment.controller;

import com.example.hackerthon.comment.dto.CommentRequestDto;
import com.example.hackerthon.comment.dto.CommentResponseDto;
import com.example.hackerthon.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts/{postId}/comments")
public class CommentController {

    private final CommentService commentService;

    // 댓글/대댓글 등록
    @PostMapping
    public CommentResponseDto createComment(@PathVariable Long postId,
                                            @RequestBody CommentRequestDto dto,
                                            Authentication authentication) {
        return commentService.createComment(postId, dto, authentication);
    }

    // 댓글/대댓글 수정
    @PutMapping("/{commentId}")
    public CommentResponseDto updateComment(@PathVariable Long postId,
                                            @PathVariable Long commentId,
                                            @RequestBody CommentRequestDto dto,
                                            Authentication authentication) {
        return commentService.updateComment(commentId, dto, authentication);
    }

    // 댓글/대댓글 삭제
    @DeleteMapping("/{commentId}")
    public void deleteComment(@PathVariable Long postId,
                              @PathVariable Long commentId,
                              Authentication authentication) {
        commentService.deleteComment(commentId, authentication);
    }

    // 게시글의 전체 댓글/대댓글 조회
    @GetMapping
    public List<CommentResponseDto> getComments(@PathVariable Long postId) {
        return commentService.getCommentsByPost(postId);
    }
}
