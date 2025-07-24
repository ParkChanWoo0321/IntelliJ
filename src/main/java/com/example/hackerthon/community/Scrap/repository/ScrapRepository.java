package com.example.hackerthon.community.Scrap.repository;

import com.example.hackerthon.community.Scrap.entity.Scrap;
import com.example.hackerthon.community.post.entity.Post;
import com.example.hackerthon.Login.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ScrapRepository extends JpaRepository<Scrap, Long> {
    Optional<Scrap> findByUserAndPost(User user, Post post);
    List<Scrap> findByUser(User user);

    // ⭐️ 게시글 스크랩 수 조회용
    int countByPost(Post post); // ✅ 이거만 추가하면 끝!
}
