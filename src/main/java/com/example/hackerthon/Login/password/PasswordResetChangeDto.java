package com.example.hackerthon.Login.password;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordResetChangeDto {
    private String email;
    private String newPassword;
}
