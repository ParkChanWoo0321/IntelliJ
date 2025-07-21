package com.example.hackerthon.comment.service;

import com.example.hackerthon.Notification.service.NotificationService;
import com.example.hackerthon.Login.entity.User;
import com.example.hackerthon.Login.repository.UserRepository;
import com.example.hackerthon.comment.entity.Comment;
import com.example.hackerthon.comment.dto.CommentRequestDto;
import com.example.hackerthon.comment.dto.CommentResponseDto;
import com.example.hackerthon.comment.repository.CommentRepository;
import com.example.hackerthon.post.community.entity.Post;
import com.example.hackerthon.post.community.repository.PostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    // 댓글/대댓글 등록
    @Transactional
    public CommentResponseDto createComment(Long postId, CommentRequestDto dto, Authentication auth) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글 없음"));

        Comment parent = null;
        if (dto.getParentId() != null) {
            parent = commentRepository.findById(dto.getParentId())
                    .orElseThrow(() -> new IllegalArgumentException("부모 댓글 없음"));
        }

        // [1] User 객체로 author 지정
        String username = auth.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        Comment comment = Comment.builder()
                .content(dto.getContent())
                .author(user) // User 타입!
                .post(post)
                .parent(parent)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        commentRepository.save(comment);

        // 알림 처리 (댓글/대댓글)
        User sender = user; // 이미 조회함

        // 대댓글인 경우: 부모 댓글 작성자에게 알림
        if (parent != null) {
            User parentUser = parent.getAuthor();
            if (parentUser != null && !parentUser.getUsername().equals(username)) {
                String msg = sender.getNickname() + "님이 회원님의 댓글에 답글을 남겼습니다.";
                notificationService.notify(
                        parentUser, sender, msg, post.getId(), parent.getId());
            }
        }
        // 댓글인 경우: 게시글 작성자에게 알림
        else {
            User postUser = post.getAuthor();
            if (postUser != null && !postUser.getUsername().equals(username)) {
                String msg = sender.getNickname() + "님이 회원님의 글에 댓글을 남겼습니다.";
                notificationService.notify(
                        postUser, sender, msg, post.getId(), comment.getId());
            }
        }

        return toDto(comment);
    }

    // 댓글/대댓글 수정
    @Transactional
    public CommentResponseDto updateComment(Long commentId, CommentRequestDto dto, Authentication auth) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글 없음"));

        String username = auth.getName();

        // author(User) 비교
        if (!comment.getAuthor().getUsername().equals(username)) {
            throw new RuntimeException("수정 권한 없음");
        }
        comment.setContent(dto.getContent());
        comment.setUpdatedAt(LocalDateTime.now());
        return toDto(comment);
    }

    // 댓글/대댓글 삭제
    @Transactional
    public void deleteComment(Long commentId, Authentication auth) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글 없음"));

        String username = auth.getName();

        if (!comment.getAuthor().getUsername().equals(username)) {
            throw new RuntimeException("삭제 권한 없음");
        }
        commentRepository.delete(comment);
    }

    // 게시글의 전체 댓글 + 대댓글 계층 구조로 조회
    public List<CommentResponseDto> getCommentsByPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글 없음"));
        List<Comment> comments = commentRepository.findByPostAndParentIsNull(post);
        return comments.stream().map(this::toDtoWithReplies).collect(Collectors.toList());
    }

    // 엔티티 → DTO 변환 (계층 구조)
    private CommentResponseDto toDtoWithReplies(Comment comment) {
        return CommentResponseDto.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .author(comment.getAuthor().getNickname()) // 또는 getUsername(), 원하는 정보로!
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .parentId(comment.getParent() != null ? comment.getParent().getId() : null)
                .replies(comment.getReplies().stream()
                        .map(this::toDtoWithReplies)
                        .collect(Collectors.toList()))
                .build();
    }
    // 엔티티 → DTO (단일)
    private CommentResponseDto toDto(Comment comment) {
        return toDtoWithReplies(comment);
    }
}
