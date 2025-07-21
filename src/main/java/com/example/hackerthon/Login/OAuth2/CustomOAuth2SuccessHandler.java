package com.example.hackerthon.Login.OAuth2;

import com.example.hackerthon.Login.entity.User;
import com.example.hackerthon.Login.jwt.JwtProvider;
import com.example.hackerthon.Login.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        DefaultOAuth2User oAuth2User = (DefaultOAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String email = extractEmail(attributes);
        if (email == null) {
            throw new IllegalArgumentException("OAuth2 로그인 정보에 이메일이 없습니다.");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("유저 정보가 없습니다."));

        // JwtProvider#createToken(User) 메서드로 토큰 생성
        String token = jwtProvider.createToken(user);

        // 프론트 리다이렉트 URL (운영 환경에 맞게 변경)
        response.setContentType("text/plain;charset=UTF-8");
        response.getWriter().write("JWT Token: " + token);
    }

    /**
     * 네이버(response), 구글(email), 카카오(kakao_account)에서
     * 이메일만 꺼내는 헬퍼 메서드
     */
    @SuppressWarnings("unchecked")
    private String extractEmail(Map<String, Object> attributes) {
        // 네이버
        if (attributes.containsKey("response")) {
            Object respObj = attributes.get("response");
            if (respObj instanceof Map<?, ?>) {
                Map<String, Object> resp = (Map<String, Object>) respObj;
                return (String) resp.get("email");
            }
        }
        // 구글
        else if (attributes.containsKey("email")) {
            return (String) attributes.get("email");
        }
        // 카카오
        else if (attributes.containsKey("kakao_account")) {
            Object accountObj = attributes.get("kakao_account");
            if (accountObj instanceof Map<?, ?>) {
                Map<String, Object> account = (Map<String, Object>) accountObj;
                return (String) account.get("email");
            }
        }
        return null;
    }
}
