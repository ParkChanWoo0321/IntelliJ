package com.example.hackerthon.community.Like.service;

import com.example.hackerthon.Login.entity.User;
import com.example.hackerthon.Login.repository.UserRepository;
import com.example.hackerthon.Notification.service.NotificationService;
import com.example.hackerthon.community.Like.dto.LikeResponse;
import com.example.hackerthon.community.Like.entity.Like;
import com.example.hackerthon.community.post.entity.Post;
import com.example.hackerthon.community.Like.repository.LikeRepository;
import com.example.hackerthon.community.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Transactional
    public LikeResponse toggleLike(Long postId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글 없음"));

        Optional<Like> likeOpt = likeRepository.findByUserAndPost(user, post);
        boolean liked;
        if (likeOpt.isPresent()) {
            likeRepository.delete(likeOpt.get());
            liked = false;
        } else {
            likeRepository.save(Like.builder().user(user).post(post).build());
            liked = true;

            // 알림 추가 (자신이 자기 글에 누르면 제외)
            if (!post.getAuthor().getUsername().equals(user.getUsername())) {
                String msg = user.getNickname() + "님이 회원님의 글을 좋아합니다.";
                notificationService.notify(
                        post.getAuthor(),   // 알림 받는 사람 (게시글 작성자)
                        user,               // 알림 보낸 사람 (좋아요 누른 사람)
                        msg,
                        post.getId(),
                        null                // 댓글 없음
                );
            }
        }
        int count = likeRepository.countByPost(post);
        LikeResponse resp = new LikeResponse();
        resp.setPostId(postId);
        resp.setLikeCount(count);
        resp.setLiked(liked);
        return resp;
    }

    // 내가 좋아요 누른 글(Post) 목록 반환
    @Transactional(readOnly = true)
    public List<Post> getLikedPosts(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));
        List<Like> likes = likeRepository.findByUser(user);
        return likes.stream()
                .map(Like::getPost)
                .collect(Collectors.toList());
    }
}
