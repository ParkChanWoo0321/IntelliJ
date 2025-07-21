package com.example.hackerthon.post.community.dto;

import com.example.hackerthon.post.community.entity.Post;
import lombok.Getter;
import lombok.Setter;
import java.util.List;
import java.util.stream.Collectors;

@Getter @Setter
public class PostResponse {
    private Long id;
    private String title;
    private String content;
    private String authorUsername;
    private String authorNickname;
    private String createdAt;
    private String updatedAt;
    private List<PostMediaDto> mediaList;
    private int likeCount;
    private int scrapCount;
    private int commentCount;

    public PostResponse(Post post, int likeCount, int scrapCount, int commentCount) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.authorUsername = post.getAuthor().getUsername();
        this.authorNickname = post.getAuthor().getNickname();
        this.createdAt = post.getCreatedAt() != null ? post.getCreatedAt().toString() : null;
        this.updatedAt = post.getUpdatedAt() != null ? post.getUpdatedAt().toString() : null;
        this.likeCount = likeCount;
        this.scrapCount = scrapCount;
        this.commentCount = commentCount;
        if (post.getMediaList() != null) {
            this.mediaList = post.getMediaList().stream()
                    .map(media -> {
                        PostMediaDto dto = new PostMediaDto();
                        dto.setUrl(media.getUrl());
                        dto.setType(media.getType());
                        return dto;
                    })
                    .collect(Collectors.toList());
        }
    }
}
