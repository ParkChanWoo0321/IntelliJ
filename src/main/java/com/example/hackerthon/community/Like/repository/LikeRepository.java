package com.example.hackerthon.community.Like.repository;

import com.example.hackerthon.community.Like.entity.Like;
import com.example.hackerthon.community.post.entity.Post;
import com.example.hackerthon.Login.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByUserAndPost(User user, Post post);
    int countByPost(Post post);
    void deleteByUserAndPost(User user, Post post);
    List<Like> findByUser(User user);
}
