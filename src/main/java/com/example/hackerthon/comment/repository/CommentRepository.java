package com.example.hackerthon.comment.repository;

import com.example.hackerthon.Login.entity.User;
import com.example.hackerthon.comment.entity.Comment;
import com.example.hackerthon.post.community.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostAndParentIsNull(Post post); // 게시글의 댓글(최상위)
    List<Comment> findByParent(Comment parent);          // 대댓글 목록
    int countByPost(Post post);
    List<Comment> findByAuthor(User author);
}
