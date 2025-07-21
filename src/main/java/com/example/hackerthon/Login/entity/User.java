package com.example.hackerthon.Login.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false, unique = true)
    private String username;

    @Setter
    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    @Setter
    @Column(nullable = false, unique = true)
    private String nickname;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column
    private String profileImageUrl;//= "/profile_uploads/default_profile.png";
    // 기본 프사 적용 하는법
    // static에 기본 이미지 파일 추가
    //static/profile_uploads/default_profile.png

    @Column(nullable = false)
    private String provider;

    public enum Role {
        USER, ADMIN
    }
}
