package com.example.hackerthon.community.post.service;

import com.example.hackerthon.Login.entity.User;
import com.example.hackerthon.Login.repository.UserRepository;
import com.example.hackerthon.community.Like.repository.LikeRepository;
import com.example.hackerthon.community.Scrap.repository.ScrapRepository;
import com.example.hackerthon.comment.repository.CommentRepository;
import com.example.hackerthon.community.post.dto.PostRequest;
import com.example.hackerthon.community.post.dto.PostResponse;
import com.example.hackerthon.community.post.entity.Post;
import com.example.hackerthon.community.post.entity.PostMedia;
import com.example.hackerthon.community.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final ScrapRepository scrapRepository;
    private final CommentRepository commentRepository;

    // 글 작성
    @Transactional
    public Post createPost(PostRequest dto, List<MultipartFile> files, String username) throws IOException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));

        Post post = Post.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .author(user)
                .build();

        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                String fileUrl = saveFile(file);
                String type = determineMediaType(file);
                PostMedia media = PostMedia.builder()
                        .url(fileUrl)
                        .type(type)
                        .post(post)
                        .build();
                post.getMediaList().add(media);
            }
        }

        return postRepository.save(post);
    }

    // 글 수정
    @Transactional
    public Post updatePost(Long postId, PostRequest dto, List<MultipartFile> files, String username) throws IOException {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글 없음"));

        if (!post.getAuthor().getUsername().equals(username)) {
            throw new SecurityException("본인만 수정 가능");
        }

        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());

        if (files != null && !files.isEmpty()) {
            post.getMediaList().clear();

            for (MultipartFile file : files) {
                String fileUrl = saveFile(file);
                String type = determineMediaType(file);
                PostMedia media = PostMedia.builder()
                        .url(fileUrl)
                        .type(type)
                        .post(post)
                        .build();
                post.getMediaList().add(media);
            }
        }

        return postRepository.save(post);
    }

    // 글 삭제
    public void deletePost(Long postId, String username) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글 없음"));

        if (!post.getAuthor().getUsername().equals(username)) {
            throw new SecurityException("본인만 삭제 가능");
        }

        postRepository.delete(post);
    }

    // 단일 글 조회 (좋아요/스크랩/댓글 수 포함)
    public PostResponse getPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글 없음"));

        int likeCount = likeRepository.countByPost(post);
        int scrapCount = scrapRepository.countByPost(post);
        int commentCount = commentRepository.countByPost(post);

        return new PostResponse(post, likeCount, scrapCount, commentCount);
    }

    // 전체 글 목록 조회
    public List<PostResponse> getAllPostResponses() {
        return postRepository.findAll().stream()
                .map(post -> new PostResponse(
                        post,
                        likeRepository.countByPost(post),
                        scrapRepository.countByPost(post),
                        commentRepository.countByPost(post)
                ))
                .toList();
    }

    // 내가 쓴 글 조회
    public List<PostResponse> getMyPosts(String username) {
        return postRepository.findByAuthor_Username(username).stream()
                .map(post -> new PostResponse(
                        post,
                        likeRepository.countByPost(post),
                        scrapRepository.countByPost(post),
                        commentRepository.countByPost(post)
                )).toList();
    }

    // 검색 결과
    public List<PostResponse> searchPostResponses(String keyword) {
        return postRepository.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(keyword, keyword)
                .stream()
                .map(post -> new PostResponse(
                        post,
                        likeRepository.countByPost(post),
                        scrapRepository.countByPost(post),
                        commentRepository.countByPost(post)
                ))
                .toList();
    }

    // 파일 저장 (로컬)
    private String saveFile(MultipartFile file) throws IOException {
        String uploadDir = "uploads/";
        File dir = new File(uploadDir);
        if (!dir.exists()) dir.mkdirs();

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        File dest = new File(uploadDir + fileName);
        file.transferTo(dest);

        return "/" + uploadDir + fileName;
    }

    // 미디어 타입 판단
    private String determineMediaType(MultipartFile file) {
        String originalName = Objects.requireNonNull(file.getOriginalFilename()).toLowerCase();
        if (originalName.endsWith(".mp4") || originalName.endsWith(".mov") || originalName.endsWith(".avi")) {
            return "VIDEO";
        } else {
            return "IMAGE";
        }
    }
}
