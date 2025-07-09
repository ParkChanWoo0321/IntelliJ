package com.example.running.repository;

import com.example.running.entity.Post;
import com.example.running.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByUser(User user);
    List<Post> findAllByTags_Name(String tagName);  // 태그 이름으로 게시글 검색
}
