package com.example.hackerthon.Login.jwt;

import com.example.hackerthon.Login.entity.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys; // 추가
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets; // 추가
import java.security.Key; // 추가
import java.util.Date;

@Component
public class JwtProvider {

    public String createToken(User user) {
        // 32자 이상! (환경변수로 관리 권장)
        String SECRET_KEY = "MySuperSecretKeyForJwtSigning123!@#2024";
        long EXPIRATION_TIME = 1000 * 60 * 60 * 24;

        // 최신 jjwt 방식 (Key 객체 사용)
        Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("role", user.getRole().name())
                .claim("username", user.getUsername())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key, SignatureAlgorithm.HS256) // <- 최신 방식!
                .compact();
    }
}
