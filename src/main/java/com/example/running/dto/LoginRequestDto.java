package com.example.running.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor


public class LoginRequestDto {
    private String username;
    private String password;
}
