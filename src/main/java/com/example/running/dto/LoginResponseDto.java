package com.example.running.dto;

import lombok.Getter;
import lombok.Setter;
import com.example.running.entity.User;

@Getter @Setter
public class LoginResponseDto {
    private String username;
    private String password;
    private Long userId;

    public LoginResponseDto(User user) {
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.userId = user.getUserId();
    }
}
