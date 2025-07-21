package com.example.hackerthon.Login.dto;
import com.example.hackerthon.post.community.dto.PostResponse;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter @Setter
public class MyInfoResponseDto {
    private String username;
    private String email;
    private String nickname;
    private String role;
    private String profileImageUrl;
    private List<PostResponse> myPosts;
    private List<PostResponse> myScraps;
    private List<PostResponse> myLikes;
    private List<PostResponse> myCommentedPosts;
}
