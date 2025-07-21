package com.example.hackerthon.post.Scrap.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ScrapResponse {
    private Long postId;
    private boolean scrapped;
}
