package com.example.hackerthon.community.Scrap.service;

import com.example.hackerthon.Login.entity.User;
import com.example.hackerthon.Login.repository.UserRepository;
import com.example.hackerthon.community.Scrap.entity.Scrap;
import com.example.hackerthon.community.post.entity.Post;
import com.example.hackerthon.community.Scrap.repository.ScrapRepository;
import com.example.hackerthon.community.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScrapService {
    private final ScrapRepository scrapRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public boolean toggleScrap(Long postId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글 없음"));

        Optional<Scrap> scrapOpt = scrapRepository.findByUserAndPost(user, post);
        if (scrapOpt.isPresent()) {
            scrapRepository.delete(scrapOpt.get());
            return false; // 스크랩 취소됨
        } else {
            scrapRepository.save(Scrap.builder().user(user).post(post).build());
            return true; // 스크랩 추가됨
        }
    }

    // ⭐️ 내가 스크랩한 글(Post) 목록 반환
    @Transactional(readOnly = true)
    public List<Post> getScrappedPosts(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));
        List<Scrap> scraps = scrapRepository.findByUser(user);
        return scraps.stream()
                .map(Scrap::getPost)
                .collect(Collectors.toList());
    }
}
