package com.example.hackerthon.Login.password;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordResetVerifyDto {
    private String email;
    private String code;
}
