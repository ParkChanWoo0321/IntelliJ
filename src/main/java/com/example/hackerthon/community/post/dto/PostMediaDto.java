package com.example.hackerthon.community.post.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PostMediaDto {
    private String url;
    private String type; // "IMAGE" or "VIDEO"
}
