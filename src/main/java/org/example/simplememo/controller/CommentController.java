package org.example.simplememo.controller;

import lombok.AllArgsConstructor;
import org.example.simplememo.CommonResponse;
import org.example.simplememo.dto.CommentRequestDto;
import org.example.simplememo.dto.CommentResponseDto;
import org.example.simplememo.entity.Comment;
import org.example.simplememo.service.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comments")
@AllArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/{memoId}")
    public ResponseEntity<CommonResponse<CommentResponseDto>> postComment(@PathVariable Long memoId, @RequestBody CommentRequestDto request) {
        Comment comment = commentService.createComment(memoId, request);
        CommentResponseDto response = new CommentResponseDto(comment);
        return ResponseEntity.ok()
                .body(CommonResponse.<CommentResponseDto>builder()
                        .statusCode(200)
                        .msg("게시글 번호 " + memoId + "댓글 생성이 완료되었습니다.")
                        .data(response)
                        .build());
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<CommonResponse<CommentResponseDto>> updateComment(@PathVariable Long commentId, @RequestBody CommentRequestDto request) {
        Comment comment = commentService.updateComment(commentId, request);
        CommentResponseDto response = new CommentResponseDto(comment);
        return ResponseEntity.ok()
                .body(CommonResponse.<CommentResponseDto>builder()
                        .statusCode(200)
                        .msg("댓글 수정이 완료되었습니다.")
                        .data(response)
                        .build());
    }

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
