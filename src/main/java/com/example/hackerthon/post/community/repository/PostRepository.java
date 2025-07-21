package com.example.hackerthon.post.community.repository;

import com.example.hackerthon.post.community.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(String title, String content);
    List<Post> findByAuthor_Username(String username);

}

