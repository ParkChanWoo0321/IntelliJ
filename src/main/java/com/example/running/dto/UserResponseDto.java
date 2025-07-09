package com.example.running.dto;

import com.example.running.entity.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponseDto {
    private String username;
    private String nickname;

    public UserResponseDto(String username, String nickname) {
        this.nickname = nickname;
        this.username = username;
    }
}
