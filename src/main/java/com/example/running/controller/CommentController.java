package com.example.running.controller;

import com.example.running.CommonResponse;
import com.example.running.dto.CommentRequestDto;
import com.example.running.dto.CommentResponseDto;
import com.example.running.entity.Comment;
import com.example.running.service.CommentService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comments")
@AllArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // 댓글 작성
    @PostMapping("/{postId}")
    public ResponseEntity<CommonResponse<CommentResponseDto>> postComment(
            @PathVariable Long postId,
            @RequestParam Long userId,
            @RequestBody CommentRequestDto request) {

        Comment comment = commentService.createComment(postId, userId, request);
        CommentResponseDto response = new CommentResponseDto(comment);

        return ResponseEntity.ok()
                .body(CommonResponse.<CommentResponseDto>builder()
                        .statusCode(200)
                        .msg("댓글 작성이 완료되었습니다.")
                        .data(response)
                        .build());
    }

    // 댓글 수정
    @PutMapping("/{commentId}")
    public ResponseEntity<CommonResponse<CommentResponseDto>> updateComment(
            @PathVariable Long commentId,
            @RequestBody CommentRequestDto request) {

        Comment comment = commentService.updateComment(commentId, request);
        CommentResponseDto response = new CommentResponseDto(comment);

        return ResponseEntity.ok()
                .body(CommonResponse.<CommentResponseDto>builder()
                        .statusCode(200)
                        .msg("댓글 수정이 완료되었습니다.")
                        .data(response)
                        .build());
    }

    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    public ResponseEntity<CommonResponse<Void>> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);

        return ResponseEntity.ok()
                .body(CommonResponse.<Void>builder()
                        .statusCode(200)
                        .msg("댓글 삭제가 완료되었습니다.")
                        .build());
    }
}
