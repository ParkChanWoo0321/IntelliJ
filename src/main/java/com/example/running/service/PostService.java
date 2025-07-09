package com.example.running.service;

import com.example.running.dto.PostResponseDto;
import com.example.running.dto.PostUpdateRequestDto;
import com.example.running.entity.Post;
import com.example.running.entity.PostImage;
import com.example.running.entity.Tag;
import com.example.running.entity.User;
import com.example.running.repository.PostImageRepository;
import com.example.running.repository.PostRepository;
import com.example.running.repository.TagRepository;
import com.example.running.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostImageRepository postImageRepository;
    private final TagRepository tagRepository;
    private final FileStorageService fileStorageService;

    // 게시글 DTO 변환 (중복 제거)
    private PostResponseDto convertToDto(Post post) {
        return new PostResponseDto(
                post.getPostId(),
                post.getTitle(),
                post.getContent(),
                post.getCreatedAt().toString(),
                post.getImages().stream().map(PostImage::getImageUrl).collect(Collectors.toList()),
                post.getTags().stream().map(Tag::getName).collect(Collectors.toList()),
                post.getUser().getUserId(),
                post.getUser().getNickname()
        );
    }

    // 내 게시글 조회
    @Transactional(readOnly = true)
    public List<PostResponseDto> getPostsByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        List<Post> posts = postRepository.findAllByUser(user);
        return posts.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    // 태그 문자열 -> 리스트 변환
    private List<String> parseTagString(String tags) {
        if (tags == null || tags.isBlank()) return Collections.emptyList();
        return Arrays.stream(tags.trim().split("\\s+"))
                .map(tag -> tag.startsWith("#") ? tag.substring(1) : tag)
                .filter(tag -> !tag.isBlank())
                .collect(Collectors.toList());
    }

    // 게시글 작성
    @Transactional
    public PostResponseDto createPost(String title, String content, String tags,
                                      MultipartFile[] images, Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Post post = new Post();
        post.setTitle(title);
        post.setContent(content);
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());
        post.setUser(user);

        List<String> tagNames = parseTagString(tags);
        handleTags(post, tagNames);
        Post savedPost = postRepository.save(post);

        handleImages(savedPost, images);
        return convertToDto(savedPost);
    }

    // 게시글 수정
    @Transactional
    public PostResponseDto updatePost(Long postId, PostUpdateRequestDto updateRequestDto, MultipartFile[] images) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

        post.setTitle(updateRequestDto.getTitle());
        post.setContent(updateRequestDto.getContent());
        post.setUpdatedAt(LocalDateTime.now());

        List<String> tagNames = parseTagString(updateRequestDto.getTags());
        handleTags(post, tagNames);

        handleImages(post, images);
        postRepository.save(post);

        return convertToDto(post);
    }

    // 태그로 게시글 검색
    @Transactional(readOnly = true)
    public List<PostResponseDto> getPostsByTagName(String tagName) {
        List<Post> posts = postRepository.findAllByTags_Name(tagName);
        return posts.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    // 게시글 삭제
    @Transactional
    public void deletePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));
        postRepository.delete(post); // 연관 이미지 삭제는 orphanRemoval=true 설정
    }

    // 태그 처리
    private void handleTags(Post post, List<String> tagNames) {
        List<Tag> existingTags = new ArrayList<>(post.getTags());
        for (Tag tag : existingTags) post.removeTag(tag);

        if (tagNames != null) {
            for (String tagName : tagNames) {
                Tag tag = tagRepository.findByName(tagName)
                        .orElseGet(() -> tagRepository.save(new Tag(tagName)));
                post.addTag(tag);
            }
        }
    }

    // 이미지 처리
    private void handleImages(Post post, MultipartFile[] images) {
        List<PostImage> existingImages = new ArrayList<>(post.getImages());
        for (PostImage image : existingImages) {
            post.removeImage(image);
            postImageRepository.delete(image);
        }

        if (images != null) {
            for (MultipartFile image : images) {
                try {
                    String imageUrl = fileStorageService.uploadFile(image);
                    PostImage postImage = new PostImage(imageUrl,post);
                    postImageRepository.save(postImage);
                } catch (Exception e) {
                    throw new RuntimeException("이미지 업로드 중 오류가 발생했습니다.", e);
                }
            }
        }
    }
}
