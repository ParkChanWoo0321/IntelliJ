package com.example.hackerthon.Login.password;

public class PasswordValidator {
    // 8자 이상, 영문, 숫자, 특수문자 각각 1개 이상 포함
    private static final String PASSWORD_PATTERN =
            "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=`~{}\\[\\]:\";'<>?,./]).{8,}$";

    public static boolean isValid(String password) {
        return password != null && password.matches(PASSWORD_PATTERN);
    }
}
